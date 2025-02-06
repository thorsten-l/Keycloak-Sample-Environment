#!/usr/bin/env python3
"""
Diese Anwendung implementiert einen Flask-basierten OIDC/OAuth2 Client.
Sie lädt Konfigurationen, initialisiert den OIDC Service, richtet Session-Handling
ein und definiert diverse HTTP-Routen zum Login, Logout, Backchannel-Logout und
zur Darstellung der Hauptanwendung.
"""

# Import benötigter Module und Bibliotheken
import os
import logging

import javaproperties  # type: ignore # zum Laden von Properties-Dateien
import redis  # type: ignore # für Redis-Datenbank-Operationen
import requests # type: ignore # für HTTP-Anfragen

from flask import Flask, Response, redirect, render_template, request, session, url_for # type: ignore # Flask-Funktionen
from flask_session import Session # type: ignore # für serverseitige Sessions

from OidcService import OidcService  # Import des OIDC Services

# Erstellen der Flask-Anwendung und Konfigurieren des statischen Ordners
app = Flask(__name__, static_folder='/work/static', static_url_path='/static')
# Laden der Konfiguration aus einem Konfigurationsobjekt
app.config.from_object('config')

# Initialisieren der Redis-Verbindung für Sessions
redis_conf = app.config['REDIS_CONFIG']
redis_db = app.config['SESSION_REDIS'] = redis.StrictRedis(
    host=redis_conf["host"],
    port=redis_conf["port"],
    charset=redis_conf["charset"],
    decode_responses=redis_conf["decode_responses"]
)

# Konfigurieren des Loggers zur Ausgabe von Logging-Nachrichten
logging.basicConfig(
    level=logging.DEBUG,  # Log-Level: DEBUG und höher
    format='%(asctime)s - %(levelname)s - %(message)s'
)

# Initialisieren von serverseitigen Sessions in Flask
Session(app)

# --- Laden von Nachrichten aus properties-Dateien ---
def load_messages_properties(filename="messages.properties"):
    # Lädt Übersetzungen/Meldungen aus einer Properties-Datei
    try:
        with open(filename, "r", encoding="utf-8") as fp:
            return javaproperties.load(fp)
    except Exception as e:
        logging.error("Fehler beim Laden von messages.properties:", e)
        return {}

MESSAGES = load_messages_properties()

@app.context_processor
def inject_messages():
    # Stellt die geladenen Messages global in den Templates bereit
    return dict(messages=MESSAGES)

# --- Initialisieren des OIDC Services ---
# Erzeugt eine Instanz des OidcService zur Handhabung von OIDC/OAuth2 Flows.
oidc_service = OidcService(app.config, redis_db)

def initialize_oidc():
    # Führt die Initialisierung der OIDC-Konfiguration aus, indem Discovery-Daten geladen werden.
    oidc_service.initialize()

# --- Flask Routen ---

@app.route("/")
def home():
    # Startseite: Prüft, ob ein gültiger Login vorliegt und leitet ggf. zur App um.
    logging.debug("/")
    tokens = session.get("oauth2_tokens")
    if tokens and tokens.get("id_token"):
        return redirect(url_for("app_page"))

    # Ermittelt die aktuelle Session-ID und generiert die Login-URL über den OIDC Service.
    session_id = oidc_service.get_session_id(request)
    (login_url, oauth2_code_challenge) = oidc_service.get_login_url(session)

    # Modell zur Übergabe an das Template
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
    # Route für den Login-Callback: Delegiert die Verarbeitung an den OIDC Service.
    logging.debug("/oidc-login")
    # Übergibt Request und Session an den Service für Login-Handling.
    redirect_url, error = oidc_service.handle_login(request, session)
    if error:
        print(error)
        return redirect(url_for("home"))
    return redirect(redirect_url)

@app.route("/oidc-logout")
def oidc_logout():
    # Route zum Logout: Löscht die Session in Redis und leert die Flask-Session.
    logging.debug("/oidc-logout")
    session_id = oidc_service.get_session_id(request)
    oidc_service.delete_session_from_redis(session_id)
    session.clear()
    return redirect(url_for("home"))

@app.route("/oidc-backchannel-logout", methods=["POST"])
def oidc_backchannel_logout():
    # Route für den Backchannel-Logout: Der OIDC Service verarbeitet das Logout-Token.
    logging.debug("/oidc-backchannel-logout")
    oidc_service.handle_backchannel_logout(request)
    return Response(status=200)

@app.route("/app")
def app_page():
    # Hauptanwendung: Stellt geschützte Seiteninhalte bereit, wenn der User eingeloggt ist.
    logging.debug("/app")
    tokens = session.get("oauth2_tokens")
    if not tokens or not tokens.get("id_token"):
        return redirect(url_for("home"))

    # Erzeugt Log-Out URL und dekodiert die JWT-Tokens, um Nutzerdaten im Template zu übergeben.
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
    # Initialisiert den OIDC Service und startet den Flask-Webserver.
    initialize_oidc()
    app.run(host="0.0.0.0", port=8081, debug=True)
