from mozilla_django_oidc.auth import OIDCAuthenticationBackend
from django.core.exceptions import ImproperlyConfigured, SuspiciousOperation
from django.contrib.auth import get_user_model
import jwt
import requests

from app_app7.models import UserProfile

class SoniaOIDC(OIDCAuthenticationBackend):

    def __init__(self, *args, **kwargs):
        """Initialize settings."""
        self.OIDC_OP_TOKEN_ENDPOINT = self.get_settings("OIDC_OP_TOKEN_ENDPOINT")
        self.OIDC_OP_USER_ENDPOINT = self.get_settings("OIDC_OP_USER_ENDPOINT")
        self.OIDC_OP_JWKS_ENDPOINT = self.get_settings("OIDC_OP_JWKS_ENDPOINT", None)
        self.OIDC_RP_CLIENT_ID = self.get_settings("OIDC_RP_CLIENT_ID")
        self.OIDC_RP_CLIENT_SECRET = self.get_settings("OIDC_RP_CLIENT_SECRET")
        self.OIDC_RP_SIGN_ALGO = self.get_settings("OIDC_RP_SIGN_ALGO", "HS256")
        self.OIDC_RP_IDP_SIGN_KEY = self.get_settings("OIDC_RP_IDP_SIGN_KEY", None)
        self.OIDC_OP_ISSUER_ENDPOINT = self.get_settings("OIDC_OP_ISSUER_ENDPOINT")

        if self.OIDC_RP_SIGN_ALGO.startswith("RS") and (
            self.OIDC_RP_IDP_SIGN_KEY is None and self.OIDC_OP_JWKS_ENDPOINT is None
        ):
            msg = "{} alg requires OIDC_RP_IDP_SIGN_KEY or OIDC_OP_JWKS_ENDPOINT to be configured."
            raise ImproperlyConfigured(msg.format(self.OIDC_RP_SIGN_ALGO))

        self.UserModel = get_user_model()


    def describe_user_by_claims(self, claims):
        username = claims.get("preferred_username")
        return "username {}".format(username)

    
    def filter_users_by_claims(self, claims):
        #Return all users matching the specified preferred_username
        username = claims.get("preferred_username")
        if not username:
            return self.UserModel.objects.none()
        return self.UserModel.objects.filter(username__iexact=username)
    

    def verify_claims(self, claims):
        #Verify the provided claims to decide if authentication should be allowed
        scopes = self.get_settings("OIDC_RP_SCOPES", "openid profile")
        if "preferred_username" in scopes.split():
            return "preferred_username" in claims

        return True
    

    def get_or_create_user(self, access_token, id_token, payload):
        #Returns a User instance if 1 user is found. Creates a user if not found and configured to do so
        #Returns nothing if multiple users are matched

        user_info = self.get_userinfo(access_token, id_token, payload)

        #Decode tokens. The audience needs to be included in the access token (client-dedicated-scope), otherwise the decoding of the access token fails
        kc_issuer = requests.get(self.OIDC_OP_ISSUER_ENDPOINT).json()
        kc_pub_key = "-----BEGIN PUBLIC KEY-----\n" + kc_issuer['public_key'] + "\n-----END PUBLIC KEY-----"
        decoded_id_token = jwt.decode(jwt=id_token, key=kc_pub_key, algorithms=self.OIDC_RP_SIGN_ALGO, audience=self.OIDC_RP_CLIENT_ID)
        decoded_access_token = jwt.decode(jwt=access_token, key=kc_pub_key, algorithms=self.OIDC_RP_SIGN_ALGO, audience=self.OIDC_RP_CLIENT_ID)

        claims_verified = self.verify_claims(user_info)
        if not claims_verified:
            msg = "Claims verification failed"
            raise SuspiciousOperation(msg)

        # preferred_username based filtering
        users = self.filter_users_by_claims(user_info)

        if len(users) == 1:
            return self.update_user(users[0], user_info, decoded_id_token, decoded_access_token)
        elif len(users) > 1:
            # In the rare case that two user accounts have the same email address, bail. Randomly selecting one seems really wrong.
            msg = "Multiple users returned"
            raise SuspiciousOperation(msg)
        elif self.get_settings("OIDC_CREATE_USER", True):
            user = self.create_user(user_info, decoded_id_token, decoded_access_token)
            return user
        else:
            return None


    def create_user(self, user_info, decoded_id_token, decoded_access_token):
        #Return object for a newly created user account
        username = user_info.get("preferred_username")
        email = user_info.get("email")
        
        user = self.UserModel.objects.create_user(username=username, email=email)
        UserProfile.objects.create(user=user)

        self.update_user(user, user_info, decoded_id_token, decoded_access_token)

        return user


    def update_user(self, user, user_info, decoded_id_token, decoded_access_token):
        #Update existing user with new claims, if necessary save, and return user
        user.email = user_info.get("email")
        user.first_name = user_info.get('given_name', '')
        user.last_name = user_info.get('family_name', '')
        user.save()

        if not UserProfile.objects.filter(user=user).exists():
            user_profile = UserProfile.objects.create(user=user)
        else:
            user_profile = UserProfile.objects.get(user=user)
        
        user_profile.userinfo = user_info
        user_profile.idtoken = decoded_id_token
        user_profile.accesstoken = decoded_access_token
        user_profile.save()

        return user
