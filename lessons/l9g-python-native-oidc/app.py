#!/usr/bin/env python3
import base64
import hashlib
import hmac
import json
import os
import secrets
import uuid
from urllib.parse import urlencode

import redis
import requests
import javaproperties

from flask import (Flask, Response, redirect, render_template, request,
                   session, url_for)

from flask_session import Session

from cryptography.hazmat.primitives.asymmetric import rsa, padding
from cryptography.hazmat.primitives import hashes
from cryptography.hazmat.backends import default_backend

app = Flask(__name__, static_folder='/work/static', static_url_path='/static')

# Konfiguration der Session
app.config['SECRET_KEY'] = 'dein_super_geheimer_schluessel'  # für die Signierung der Sessions
app.config['SESSION_TYPE'] = 'redis'
app.config['SESSION_KEY_PREFIX'] = 'app1-session:'  # Hier wird der Präfix definiert
app.config['SESSION_PERMANENT'] = False
app.config['SESSION_USE_SIGNER'] = True  # optional: signierte Sessions
# Konfiguration der Redis-Instanz. Passe host, port und ggf. password an.

redisDb = app.config['SESSION_REDIS'] = redis.StrictRedis(
    host='app1-redis', 
    port=6379,
    charset='utf-8',
    decode_responses=False
)

# Initialisierung der Session
Session(app)

# --- Nachrichten (messages.properties) laden ---
def load_messages_properties(filename="messages.properties"):
    try:
        with open(filename, "r", encoding="utf-8") as fp:
            return javaproperties.load(fp)
    except Exception as e:
        print("Fehler beim Laden von messages.properties:", e)
        return {}

# Globale Variable, die die Nachrichten enthält
MESSAGES = load_messages_properties()

# Mit einem Context Processor stellen wir die Nachrichten allen Templates zur Verfügung.
@app.context_processor
def inject_messages():
    return dict(messages=MESSAGES)

# --- Konfiguration (analog application.yaml) ---
OIDC_DISCOVERY_URI = "https://id.dev.sonia.de/realms/dev/.well-known/openid-configuration"
OAUTH2_REDIRECT_URI = "http://app1.dev.sonia.de:8081/oidc-login"
OAUTH2_POST_LOGOUT_REDIRECT_URI = "http://app1.dev.sonia.de:8081/oidc-logout"
OAUTH2_CLIENT_ID = "app1"
OAUTH2_CLIENT_SECRET = "x45mpvfzvPU5utH7MvNclj2vtilaXW0i"
OAUTH2_CLIENT_SCOPE = "openid profile email roles"

# Diese Werte werden per Discovery (und evtl. Fallback) belegt
oauth2_authorization_endpoint = None
oauth2_token_endpoint = None
oauth2_end_session_endpoint = None
oauth2_jwks_uri = None
oidc_discovery = {}
jwks = {}

# --- Initialisierung: OIDC-Discovery und JWKS laden ---
def initialize_oidc():
    global oauth2_authorization_endpoint, oauth2_token_endpoint
    global oauth2_end_session_endpoint, oauth2_jwks_uri, oidc_discovery, jwks

    try:
        r = requests.get(OIDC_DISCOVERY_URI)
        r.raise_for_status()
        oidc_discovery = r.json()
    except Exception as e:
        print("Fehler beim Laden der OIDC Discovery-Daten:", e)
        os._exit(1)

    # Fallback: wenn einzelne Endpunkte in der Konfiguration nicht gesetzt sind
    oauth2_authorization_endpoint = oidc_discovery.get("authorization_endpoint")
    oauth2_token_endpoint = oidc_discovery.get("token_endpoint")
    oauth2_end_session_endpoint = oidc_discovery.get("end_session_endpoint")
    oauth2_jwks_uri = oidc_discovery.get("jwks_uri")

    if not all([oauth2_authorization_endpoint, oauth2_token_endpoint, oauth2_end_session_endpoint, oauth2_jwks_uri]):
        print("Nicht alle Endpunkte konnten aus der Discovery ermittelt werden.")
        os._exit(1)

    # JWKS laden
    try:
        r = requests.get(oauth2_jwks_uri)
        r.raise_for_status()
        jwks = r.json()
    except Exception as e:
        print("Fehler beim Laden der JWKS-Daten:", e)
        os._exit(1)

    # Speichern Sie die Discovery-Daten global
    app.config["OIDC_DISCOVERY"] = oidc_discovery
    print("OIDC Discovery erfolgreich geladen.")


def get_session_id() -> str:
    cookie_name = request.cookies.get(app.config['SESSION_COOKIE_NAME'])
    if not cookie_name:
        return ""
    session_id = cookie_name.split(".")[0]
    print("session_id =", session_id, flush=True)
    return session_id


# --- PKCE Funktionen ---
def generate_code_verifier():
    return base64.urlsafe_b64encode(secrets.token_bytes(32)).decode("utf-8").rstrip("=")


def generate_code_challenge(verifier: str):
    digest = hashlib.sha256(verifier.encode("utf-8")).digest()
    return base64.urlsafe_b64encode(digest).decode("utf-8").rstrip("=")


# --- JWT-Dekodierung (nur Payload) ---
def decode_jwt_payload(jwt_token: str) -> dict:
    try:
        parts = jwt_token.split(".")
        if len(parts) != 3:
            raise ValueError("Ungültiges JWT-Format")
        payload = parts[1]
        padding_needed = 4 - (len(payload) % 4)
        if padding_needed and padding_needed != 4:
            payload += "=" * padding_needed
        decoded_bytes = base64.urlsafe_b64decode(payload)
        return json.loads(decoded_bytes)
    except Exception as e:
        raise Exception("Fehler beim Dekodieren des JWT-Tokens: " + str(e))


# --- JWT-Signaturüberprüfung (RS256) ---
def get_public_key_from_jwks(kid: str):
    for key in jwks.get("keys", []):
        if key.get("kid") == kid and key.get("alg") == "RS256":
            n_b64 = key.get("n")
            e_b64 = key.get("e")
            n_int = int.from_bytes(base64.urlsafe_b64decode(n_b64 + "=="), byteorder="big")
            e_int = int.from_bytes(base64.urlsafe_b64decode(e_b64 + "=="), byteorder="big")
            public_numbers = rsa.RSAPublicNumbers(e_int, n_int)
            return public_numbers.public_key(backend=default_backend())
    return None


def validate_rs256_signature(jwt_token: str) -> bool:
    try:
        header_b64 = jwt_token.split(".")[0]
        padding_needed = 4 - (len(header_b64) % 4)
        if padding_needed and padding_needed != 4:
            header_b64 += "=" * padding_needed
        header = json.loads(base64.urlsafe_b64decode(header_b64))
        kid = header.get("kid")
        if not kid:
            print("Kein kid im JWT-Header gefunden.")
            return False
        public_key = get_public_key_from_jwks(kid)
        if not public_key:
            print("Öffentlicher Schlüssel nicht gefunden.")
            return False

        header_payload = ".".join(jwt_token.split(".")[:2]).encode("utf-8")
        signature_b64 = jwt_token.split(".")[2]
        padding_needed = 4 - (len(signature_b64) % 4)
        if padding_needed and padding_needed != 4:
            signature_b64 += "=" * padding_needed
        signature = base64.urlsafe_b64decode(signature_b64)

        public_key.verify(
            signature,
            header_payload,
            padding.PKCS1v15(),
            hashes.SHA256(),
        )
        return True
    except Exception as e:
        print("Fehler bei RS256-Signaturüberprüfung:", e)
        return False


def validate_jwt_signature(jwt_token: str) -> bool:
    try:
        header_b64 = jwt_token.split(".")[0]
        padding_needed = 4 - (len(header_b64) % 4)
        if padding_needed and padding_needed != 4:
            header_b64 += "=" * padding_needed
        header = json.loads(base64.urlsafe_b64decode(header_b64))
        alg = header.get("alg")
        if alg == "RS256":
            return validate_rs256_signature(jwt_token)
        elif alg == "HS512":
            return True  # Stub für HS512
        else:
            print("Nicht unterstützter Algorithmus:", alg)
            return False
    except Exception as e:
        print("Fehler beim Validieren des JWT:", e)
        return False


# --- Token Austausch (Authorization Code gegen Tokens) ---
def fetch_oauth2_tokens(code: str, code_verifier: str) -> dict:
    data = {
        "grant_type": "authorization_code",
        "code": code,
        "redirect_uri": OAUTH2_REDIRECT_URI,
        "client_id": OAUTH2_CLIENT_ID,
        "client_secret": OAUTH2_CLIENT_SECRET,
        "code_verifier": code_verifier,
    }
    headers = {"Content-Type": "application/x-www-form-urlencoded"}
    r = requests.post(oauth2_token_endpoint, data=data, headers=headers)
    if r.status_code // 100 == 2:
        return r.json()
    else:
        print("Token-Anfrage fehlgeschlagen:", r.text)
        return {}


# --- Endpunkte (analog zum Java WebController) ---
@app.route("/")
def home():
    print("* /", flush=True)
    tokens = session.get("oauth2_tokens")
    if tokens and tokens.get("id_token"):
        return redirect(url_for("app_page"))

    oauth2_state = str(uuid.uuid4())
    oauth2_code_verifier = generate_code_verifier()
    try:
        oauth2_code_challenge = generate_code_challenge(oauth2_code_verifier)
    except Exception as e:
        return "Fehler bei der PKCE-Code Challenge: " + str(e), 500

    session["oauth2_state"] = oauth2_state
    session["oauth2_code_verifier"] = oauth2_code_verifier

    session_id = get_session_id()
    params = {
        "client_id": OAUTH2_CLIENT_ID,
        "response_type": "code",
        "redirect_uri": OAUTH2_REDIRECT_URI,
        "scope": OAUTH2_CLIENT_SCOPE,
        "state": oauth2_state,
        "code_challenge": oauth2_code_challenge,
        "code_challenge_method": "S256",
    }
    oauth2_login_uri = oauth2_authorization_endpoint + "?" + urlencode(params)

    model = {
        "oauth2State": oauth2_state,
        "oauth2CodeVerifier": oauth2_code_verifier,
        "oauth2CodeChallenge": oauth2_code_challenge,
        "sessionId": session_id,
        "oauth2ClientId": OAUTH2_CLIENT_ID,
        "oauth2ClientScope": OAUTH2_CLIENT_SCOPE,
        "oauth2RedirectUri": OAUTH2_REDIRECT_URI,
        "oauth2AuthorizationEndpoint": oauth2_authorization_endpoint,
        "oidcDiscoveryUri": OIDC_DISCOVERY_URI,
        "oidcDiscovery": oidc_discovery,
        "oauth2LoginUri": oauth2_login_uri,
    }
    return render_template("home.html", **model)


@app.route("/oidc-login")
def oidc_login():
    print("* /oidc-login", flush=True)

    error = request.args.get("error")
    if error:
        print("Fehler bei der Anmeldung:", request.args.get("error_description"))
        return redirect(url_for("home"))

    code = request.args.get("code")
    state = request.args.get("state")
    if not code or not state:
        return redirect(url_for("home"))

    if state != session.get("oauth2_state"):
        print("Ungültiger state-Parameter")
        return redirect(url_for("home"))

    code_verifier = session.get("oauth2_code_verifier")
    tokens = fetch_oauth2_tokens(code, code_verifier)
    session["oauth2_tokens"] = tokens
    session_id = get_session_id()

    try:
        payload = decode_jwt_payload(tokens.get("id_token", ""))
        sid = payload.get("sid")
        print("sid =", sid, flush=True)
        if sid:
          redisDb.set(sid, session_id.encode('utf-8'))
    except Exception as e:
        print("Fehler beim Dekodieren des id_token:", e)

    return redirect(url_for("app_page"))

def delete_session_from_redis( session_id: str):
    if session_id:
        session_key_prefix = app.config.get('SESSION_KEY_PREFIX', '')
        full_session_key = f"{session_key_prefix}{session_id}"
        redisDb.delete(full_session_key)
        print(f"Session {full_session_key} wurde aus redis gelöscht.", flush=True)


@app.route("/oidc-logout")
def oidc_logout():
    print("* /oidc-logout", flush=True)
    session_id = get_session_id()
    delete_session_from_redis(session_id)
    session.clear()
    return redirect(url_for("home"))


@app.route("/oidc-backchannel-logout", methods=["POST"])
def oidc_backchannel_logout():
    print("* /oidc-backchannel-logout", flush=True)
    logout_token = request.get_data(as_text=True)
    try:
        payload = decode_jwt_payload(logout_token)
        sid = payload.get("sid")
        if sid:
            r_session_id = redisDb.get(sid)
            redisDb.delete(sid)
            if r_session_id:
                session_id = r_session_id.decode('utf-8')
                delete_session_from_redis(session_id)
    except Exception as e:
        print("Fehler beim Verarbeiten des logout-Tokens:", e)
    return Response(status=200)


@app.route("/app")
def app_page():
    print("* /app", flush=True)
    tokens = session.get("oauth2_tokens")
    if not tokens or not tokens.get("id_token"):
        return redirect(url_for("home"))

    params = {
        "id_token_hint": tokens.get("id_token"),
        "post_logout_redirect_uri": OAUTH2_POST_LOGOUT_REDIRECT_URI,
    }
    oauth2_logout_uri = oauth2_end_session_endpoint + "?" + urlencode(params)

    model = {
        "oauth2ClientId": OAUTH2_CLIENT_ID,
        "oauth2EndSessionEndpoint": oauth2_end_session_endpoint,
        "oauth2IdToken": tokens.get("id_token"),
        "oauth2PostLogoutRedirectUri": OAUTH2_POST_LOGOUT_REDIRECT_URI,
        "oauth2LogoutUri": oauth2_logout_uri,
        "accessTokenMap": decode_jwt_payload(tokens.get("access_token", "")),
        "idTokenMap": decode_jwt_payload(tokens.get("id_token", "")),
        "refreshTokenMap": decode_jwt_payload(tokens.get("refresh_token", "")),
    }
    return render_template("app.html", **model)


if __name__ == "__main__":
    initialize_oidc()
    app.run(host="0.0.0.0", port=8081, debug=True)
