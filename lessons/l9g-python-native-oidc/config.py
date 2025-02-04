# config.py

# Flask Sitzungseinstellungen
SECRET_KEY = "dein_super_geheimer_schluessel"
SESSION_TYPE = "redis"
SESSION_KEY_PREFIX = "app1-session:"
SESSION_PERMANENT = False
SESSION_USE_SIGNER = True

# Redis-Konfiguration
SESSION_REDIS = {
    "host": "app1-redis",
    "port": 6379,
    "charset": "utf-8",
    "decode_responses": False
}

# OIDC/OAuth2 Konfiguration
OIDC_DISCOVERY_URI = "https://id.dev.sonia.de/realms/dev/.well-known/openid-configuration"
OAUTH2_REDIRECT_URI = "http://app1.dev.sonia.de:8081/oidc-login"
OAUTH2_POST_LOGOUT_REDIRECT_URI = "http://app1.dev.sonia.de:8081/oidc-logout"
OAUTH2_CLIENT_ID = "app1"
OAUTH2_CLIENT_SECRET = "x45mpvfzvPU5utH7MvNclj2vtilaXW0i"
OAUTH2_CLIENT_SCOPE = "openid profile email roles"
