#!/usr/bin/env python3
import os
import logging

import javaproperties # type: ignore
import redis # type: ignore
import requests # type: ignore

from flask import Flask, Response, redirect, render_template, request, session, url_for # type: ignore
from flask_session import Session # type: ignore

from OidcService import OidcService  # Import our OIDC service module

app = Flask(__name__, static_folder='/work/static', static_url_path='/static')

# Load configuration from config object
app.config.from_object('config')

# Extract Redis configuration and initialize redis.StrictRedis as the session store
redis_conf = app.config['REDIS_CONFIG']
redis_db = app.config['SESSION_REDIS'] = redis.StrictRedis(
    host=redis_conf["host"],
    port=redis_conf["port"],
    charset=redis_conf["charset"],
    decode_responses=redis_conf["decode_responses"]
)

# Konfiguration des Loggers
logging.basicConfig(
    level=logging.DEBUG,  # Minimales Log-Level, d.h. alle Nachrichten ab DEBUG werden geloggt
    format='%(asctime)s - %(levelname)s - %(message)s'
)

# Initialize session handling
Session(app)

# --- Load messages from messages.properties ---
def load_messages_properties(filename="messages.properties"):
    try:
        with open(filename, "r", encoding="utf-8") as fp:
            return javaproperties.load(fp)
    except Exception as e:
        logging.error("Fehler beim Laden von messages.properties:", e)
        return {}

MESSAGES = load_messages_properties()

@app.context_processor
def inject_messages():
    return dict(messages=MESSAGES)

# --- Initialize OIDC/OAuth2 configuration via OidcService ---
oidc_service = OidcService(app.config, redis_db)

def initialize_oidc():
    oidc_service.initialize()

# --- Flask routes ---

@app.route("/")
def home():
    logging.debug("/")
    tokens = session.get("oauth2_tokens")
    if tokens and tokens.get("id_token"):
        return redirect(url_for("app_page"))

    session_id = oidc_service.get_session_id(request)
    ( login_url, oauth2_code_challenge ) = oidc_service.get_login_url(session)

    model = {
        "oauth2State": session["oauth2_state"],
        "oauth2CodeVerifier": session["oauth2_code_verifier"],
        "oauth2CodeChallenge": oauth2_code_challenge,
        "sessionId": session_id,
        "oauth2AuthorizationEndpoint": oidc_service.oauth2_authorization_endpoint,
        "oauth2ClientId": oidc_service.OAUTH2_CLIENT_ID,
        "oauth2ClientScope": oidc_service.OAUTH2_CLIENT_SCOPE,
        "oauth2RedirectUri": oidc_service.OAUTH2_REDIRECT_URI,
        "oidcDiscoveryUri": oidc_service.OIDC_DISCOVERY_URI,
        "oidcDiscovery": oidc_service.oidc_discovery,
        "oauth2LoginUri": login_url,
    }
    return render_template("home.html", **model)

@app.route("/oidc-login")
def oidc_login():
    logging.debug("/oidc-login")
    # Delegate login handling to the OidcService and catch the returned redirect URL or error.
    redirect_url, error = oidc_service.handle_login(request, session)
    if error:
        print(error)
        return redirect(url_for("home"))
    return redirect(redirect_url)

@app.route("/oidc-logout")
def oidc_logout():
    logging.debug("/oidc-logout")
    session_id = oidc_service.get_session_id(request)
    oidc_service.delete_session_from_redis(session_id)
    session.clear()
    return redirect(url_for("home"))

@app.route("/oidc-backchannel-logout", methods=["POST"])
def oidc_backchannel_logout():
    logging.debug("/oidc-backchannel-logout")
    oidc_service.handle_backchannel_logout(request)
    return Response(status=200)

@app.route("/app")
def app_page():
    logging.debug("/app")
    tokens = session.get("oauth2_tokens")
    if not tokens or not tokens.get("id_token"):
        return redirect(url_for("home"))

    logout_url = oidc_service.get_logout_url(tokens.get("id_token", ""))

    model = {
        "oauth2ClientId": oidc_service.OAUTH2_CLIENT_ID,
        "oauth2EndSessionEndpoint": oidc_service.oauth2_end_session_endpoint,
        "oauth2IdToken": tokens.get("id_token"),
        "oauth2PostLogoutRedirectUri": oidc_service.OAUTH2_POST_LOGOUT_REDIRECT_URI,
        "oauth2LogoutUri": logout_url,
        "accessTokenMap": oidc_service.decode_jwt_payload(tokens.get("access_token", "")),
        "idTokenMap": oidc_service.decode_jwt_payload(tokens.get("id_token", "")),
        "refreshTokenMap": oidc_service.decode_jwt_payload(tokens.get("refresh_token", "")),
    }
    return render_template("app.html", **model)

if __name__ == "__main__":
    initialize_oidc()
    app.run(host="0.0.0.0", port=8081, debug=True)
