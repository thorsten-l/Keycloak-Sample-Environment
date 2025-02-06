"""
OIDC Service

Diese Datei implementiert einen Service zur Handhabung von OpenID Connect (OIDC)
und OAuth2 Flows. Es werden Discovery-Daten, Token-Anfragen und Logins/Logouts
bearbeitet. Zusätzlich wird Redis zur Session-Verwaltung genutzt.
"""

import logging
import base64
import hashlib
import json
import secrets
import uuid
import requests # type: ignore
from urllib.parse import urlencode

class OidcService:
    def __init__(self, config: dict, redis_db):
        
        # Konstruktor: Initialisiert OIDC- und OAuth2-Konfiguration sowie Redis-Verbindung.
        self.config = config
        # OIDC/OAuth2 Konfiguration aus der Anwendungskonfiguration
        self.OIDC_DISCOVERY_URI = config.get("OIDC_DISCOVERY_URI")
        self.OAUTH2_REDIRECT_URI = config.get("OAUTH2_REDIRECT_URI")
        self.OAUTH2_POST_LOGOUT_REDIRECT_URI = config.get("OAUTH2_POST_LOGOUT_REDIRECT_URI")
        self.OAUTH2_CLIENT_ID = config.get("OAUTH2_CLIENT_ID")
        self.OAUTH2_CLIENT_SECRET = config.get("OAUTH2_CLIENT_SECRET")
        self.OAUTH2_CLIENT_SCOPE = config.get("OAUTH2_CLIENT_SCOPE")
        
        # Redis-Konfiguration für Session-Handling
        self.redis_db = redis_db
        self.session_key_prefix = config.get("SESSION_KEY_PREFIX")

        # Endpunkt-Variablen, die nach der Discovery gesetzt werden
        self.oauth2_authorization_endpoint = None
        self.oauth2_token_endpoint = None
        self.oauth2_end_session_endpoint = None
        self.oauth2_jwks_uri = None
        self.oidc_discovery = {}
        self.jwks = {}

    def initialize(self):
        # Lädt OIDC Discovery-Daten und JWKS; beendet die Ausführung, falls Endpunkte fehlen.
        try:
            r = requests.get(self.OIDC_DISCOVERY_URI)
            r.raise_for_status()
            self.oidc_discovery = r.json()
        except Exception as e:
            logging.critical("Fehler beim Laden der OIDC Discovery-Daten:", e)
            raise SystemExit(1)

        # Endpunkte anhand der Discovery-Daten bestimmen
        self.oauth2_authorization_endpoint = self.oidc_discovery.get("authorization_endpoint")
        self.oauth2_token_endpoint = self.oidc_discovery.get("token_endpoint")
        self.oauth2_end_session_endpoint = self.oidc_discovery.get("end_session_endpoint")
        self.oauth2_jwks_uri = self.oidc_discovery.get("jwks_uri")

        if not all([self.oauth2_authorization_endpoint, self.oauth2_token_endpoint,
                    self.oauth2_end_session_endpoint, self.oauth2_jwks_uri]):
            logging.critical("Nicht alle Endpunkte konnten aus der Discovery ermittelt werden.")
            raise SystemExit(1)

        # JWKS-Daten über den entsprechenden Endpunkt laden
        try:
            r = requests.get(self.oauth2_jwks_uri)
            r.raise_for_status()
            self.jwks = r.json()
        except Exception as e:
            logging.critical("Fehler beim Laden der JWKS-Daten:", e)
            raise SystemExit(1)

        logging.info("OIDC Discovery erfolgreich geladen.")

    def generate_state(self) -> str:
        # Erzeugt einen zufälligen State-Wert als UUID für die OAuth2-Authentifizierung.
        return str(uuid.uuid4())

    def generate_code_verifier(self) -> str:
        # Erzeugt einen zufälligen Code Verifier für den PKCE-Flow.
        return base64.urlsafe_b64encode(secrets.token_bytes(32)).decode("utf-8").rstrip("=")

    def generate_code_challenge(self, verifier: str) -> str:
        # Berechnet aus dem Code Verifier mittels SHA256-Hash und Base64-URL die Code Challenge.
        digest = hashlib.sha256(verifier.encode("utf-8")).digest()
        return base64.urlsafe_b64encode(digest).decode("utf-8").rstrip("=")

    def get_login_url(self, session) -> (str, str): # type: ignore
        # Stellt die URL für den Login zusammen und speichert zugehörige Session-Daten.
        oauth2_state = self.generate_state()  # Generiert einen zufälligen State
        oauth2_code_verifier = self.generate_code_verifier()  # Erzeugt den Code Verifier für PKCE
        oauth2_code_challenge = self.generate_code_challenge(oauth2_code_verifier)  # Berechnet die Code Challenge

        session["oauth2_state"] = oauth2_state
        session["oauth2_code_verifier"] = oauth2_code_verifier

        # Zusammensetzen der Parameter für den OAuth2 Autorisierungsendpunkt
        params = {
            "client_id": self.OAUTH2_CLIENT_ID,
            "response_type": "code",
            "redirect_uri": self.OAUTH2_REDIRECT_URI,
            "scope": self.OAUTH2_CLIENT_SCOPE,
            "state": oauth2_state,
            "code_challenge": oauth2_code_challenge,
            "code_challenge_method": "S256",
        }
        return (f"{self.oauth2_authorization_endpoint}?{urlencode(params)}", oauth2_code_challenge)

    def get_logout_url(self, id_token: str) -> str:
        # Erstellt die Logout-URL unter Einbezug des id_token und des Redirect-URIs.
        params = {
            "id_token_hint": id_token,
            "post_logout_redirect_uri": self.OAUTH2_POST_LOGOUT_REDIRECT_URI,
        }
        return f"{self.oauth2_end_session_endpoint}?{urlencode(params)}"

    def decode_jwt_payload(self, jwt_token: str) -> dict:
        # Dekodiert die Payload eines JWT-Tokens. Validiert das Format und führt Base64-Decodierung durch.
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

    def fetch_tokens(self, code: str, code_verifier: str) -> dict:
        # Ruft Tokens vom OAuth2 Token-Endpunkt ab. Bei Erfolg werden die Token als JSON zurückgeliefert.
        data = {
            "grant_type": "authorization_code",
            "code": code,
            "redirect_uri": self.OAUTH2_REDIRECT_URI,
            "client_id": self.OAUTH2_CLIENT_ID,
            "client_secret": self.OAUTH2_CLIENT_SECRET,
            "code_verifier": code_verifier,
        }
        headers = {"Content-Type": "application/x-www-form-urlencoded"}
        r = requests.post(self.oauth2_token_endpoint, data=data, headers=headers)
        if r.status_code // 100 == 2:
            return r.json()
        else:
            print("Token-Anfrage fehlgeschlagen:", r.text)
            return {}

    # --- Redis-related Functions ---
    
    def set_session_in_redis(self, sid: str, session_id: str):
        # Speichert die Zuordnung sid -> session_id in Redis.
        try:
            self.redis_db.set(sid, session_id.encode("utf-8"))
            logging.debug(f"Stored session: {sid} -> {session_id}")
        except Exception as e:
            logging.error("Fehler beim Speichern der Session in Redis:", e)

    def delete_session_from_redis(self, session_id: str):
        # Löscht die Session in Redis, basierend auf dem session_key_prefix und der session_id.
        if session_id:
            full_session_key = f"{self.session_key_prefix}{session_id}"
            try:
                self.redis_db.delete(full_session_key)
                logging.debug(f"Session {full_session_key} wurde aus redis gelöscht.")
            except Exception as e:
                logging.error("Fehler beim Löschen der Session in Redis:", e)

    def get_session_id(self, request) -> str:
        # Extrahiert die session_id aus einem Cookie, basierend auf dem in der Konfiguration definierten Namen.
        cookie_name = request.cookies.get(self.config.get('SESSION_COOKIE_NAME', 'session'))
        if not cookie_name:
            return ""
        session_id = cookie_name.split(".")[0]
        logging.debug(f"session_id = {session_id}")
        return session_id

    # --- Higher-Level OIDC Flow Handlers ---
    
    def handle_login(self, request, session) -> (str, str): # type: ignore
        # Behandelt den OIDC-Login-Callback: Validiert den State, ruft Tokens ab, dekodiert das id_token und speichert die Session in Redis.
        error = request.args.get("error")
        if error:
            print("Fehler bei der Anmeldung:", request.args.get("error_description"))
            return (None, "Login error: " + request.args.get("error_description"))

        code = request.args.get("code")
        state = request.args.get("state")
        if not code or not state:
            return (None, "Missing code or state")

        if state != session.get("oauth2_state"):
            logging.error("Ungültiger state-Parameter")
            return (None, "Invalid state parameter")

        code_verifier = session.get("oauth2_code_verifier")
        tokens = self.fetch_tokens(code, code_verifier)
        session["oauth2_tokens"] = tokens

        session_id = self.get_session_id(request)
        try:
            payload = self.decode_jwt_payload(tokens.get("id_token", ""))
            sid = payload.get("sid")
            logging.debug(f"sid = {sid}")
            if sid:
                self.set_session_in_redis(sid, session_id)
        except Exception as e:
            logging.error("Fehler beim Dekodieren des id_token:", e)
            return (None, "Error decoding id_token")

        return ("/app", None)

    def handle_backchannel_logout(self, request):
        # Verarbeitet den Backchannel Logout: Dekodiert das Logout-Token, identifiziert die Session und löscht diese in Redis.
        logout_token = request.get_data(as_text=True)
        try:
            payload = self.decode_jwt_payload(logout_token)
            sid = payload.get("sid")
            if sid:
                r_session_id = self.redis_db.get(sid)
                self.redis_db.delete(sid)
                if r_session_id:
                    session_id = r_session_id.decode("utf-8")
                    self.delete_session_from_redis(session_id)
        except Exception as e:
            logging.error("Fehler beim Verarbeiten des logout-Tokens:", e)
