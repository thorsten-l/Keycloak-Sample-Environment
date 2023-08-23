# Apache2 HTTPd + OIDC Auth Module (OpenID Connect)

## Configuration
[https://github.com/OpenIDC/mod_auth_openidc](https://github.com/OpenIDC/mod_auth_openidc)

```
LoadModule auth_openidc_module modules/mod_auth_openidc.so

OIDCProviderMetadataURL <issuer>/.well-known/openid-configuration
OIDCClientID <client_id>
OIDCClientSecret <client_secret>

OIDCRedirectURI https://<hostname>/protected/
OIDCCryptoPassphrase <password>

<Location /protected>
   AuthType openid-connect
   Require valid-user
</Location>
```
