# Keycloak Sample Environment

## Kontakt
Dr.-Ing. Thorsten Ludewig (t.ludewig@gmail.com)

## Einführung

Keycloak ist eine Open-Source-Software zur Verwaltung der Authentifizierung und Autorisierung für Webanwendungen und RESTful Webdienste. Es ist ein Single-Sign-On (SSO)-System, das es Benutzern ermöglicht, sich einmal zu authentifizieren und dann auf mehrere Anwendungen zuzugreifen, ohne sich erneut anmelden zu müssen.

Keycloak bietet eine zentrale Anlaufstelle für die Verwaltung von Benutzerkonten, Berechtigungen und Rollen. Es unterstützt eine Vielzahl von Authentifizierungstechnologien, darunter OpenID Connect, OAuth 2.0, SAML 2.0 und Kerberos. Außerdem bietet es Funktionen wie Multi-Faktor-Authentifizierung, Social Login, Benutzerverwaltung, Gruppenmanagement und Single-Sign-Out.

Keycloak kann als eigenständiger Server bereitgestellt werden oder in eine vorhandene Anwendung integriert werden. Es ist in Java geschrieben und läuft auf verschiedenen Betriebssystemen, einschließlich Windows, Linux und macOS. Keycloak bietet auch eine REST-API zur Integration mit anderen Anwendungen und Diensten.

Keycloak wird von Red Hat entwickelt und unterstützt. Es ist eine beliebte Authentifizierungslösung für Unternehmen, die eine zentrale Plattform für die Verwaltung von Benutzern und Anwendungen benötigen. (ChatGPT)

## Installation

Alle in dieser Umgebung verwendeten Beispiele beötigen *Docker* als Laufzeitumgebung.

- [Docker Desktop](https://www.docker.com/products/docker-desktop/)

Das gesamte System ist nur für den `localhost` konfiguriert, Docker und dein Lieblings-Browser müssen auf dem gleichen Rechner laufen. (z.B. deinem Notebook oder Arbeitsplatz-PC)

Bitte füge folgende Zeilen in deine `/etc/hosts` Datei ein.

```text
127.0.0.1       id.dev.sonia.de

127.0.0.1       app1.dev.sonia.de
127.0.0.1       app2.dev.sonia.de
127.0.0.1       app3.dev.sonia.de
127.0.0.1       app4.dev.sonia.de
127.0.0.1       app5.dev.sonia.de
127.0.0.1       app6.dev.sonia.de
127.0.0.1       app7.dev.sonia.de
127.0.0.1       app8.dev.sonia.de
127.0.0.1       app9.dev.sonia.de
127.0.0.1       app10.dev.sonia.de
127.0.0.1       app11.dev.sonia.de
127.0.0.1       app12.dev.sonia.de

127.0.0.1       rscs1.dev.sonia.de
```

## Starte Keycloak Umgebung

1. `cd keycloak`
2. `./bin/STARTUP.sh`

## Starte Beispiel Applikation

1. `cd samples/app...`
2. `docker compose up`

## Credentials

Beschreibung | Username | Password 
-------------|----------|---------
Keycloak Admin | admin | admin123
Keycloak Test User 1 | c1test1 | test123 
Keycloak Test User 2 | c1test2 | test123 
Keycloak Test User 3 | c1test3 | test123 
Keycloak Test User 4 | c1test4 | test123 
Keycloak Test User 5 | c1test5 | test123 
LDAP Directory Manager | cn=Directory Manager | ad1n36KCEF28wPogqvlX8FDImxfkAJBtj0XIgdvyitrmd83AITAS0JA8Uf.q3Yw6T

## URLs

ID | Beschreibung | Auth | URL
----|-------|--|----
id | Keycloak Identity Provider (IDP) | OAuth2, OIDC, SAML2 | https://id.dev.sonia.de
app1 | Springboot 2 + Keycloak Lib | OIDC | http://app1.dev.sonia.de:8081
app2 | Springboot 3 + OAuth2 Lib | OIDC | http://app2.dev.sonia.de:8082
app3 | PHP 8 + Apache 2 | OIDC | http://app3.dev.sonia.de:8083
app4 | Apache 2 + mod_auth_openidc | OIDC | http://app4.dev.sonia.de:8084
app5 | NGINX / OpenResty | OIDC | http://app5.dev.sonia.de:8085
app6 | Springboot 3 + SpringSecurity SAML2 (OpenSAML) | SAML2 | http://app6.dev.sonia.de:8086
app7 | Python 3.9 + Django + mozilla-django-oidc  | OIDC | http://app7.dev.sonia.de:8087
app8 | Springboot 3 + OAuth2 Lib / Springboot 3 Resource Server | OIDC | http://app8.dev.sonia.de:8088
app9 | Jakarta EE 10 + Payara ME 6.2023.4 | OIDC | http://app9.dev.sonia.de:8089
app10 | Springboot 3.1 + OAuth2 Lib | OIDC | http://app10.dev.sonia.de:8090
app11 | JavaScrip + OIDC Client | OIDC | http://app11.dev.sonia.de:8091

## Referenzen

### Java

- [https://bell-sw.com/pages/downloads/](https://bell-sw.com/pages/downloads/)

### Spring Boot
- [https://spring.io/projects/spring-boot](https://spring.io/projects/spring-boot)
- [https://start.spring.io/](https://start.spring.io/)
- [Servlet - https://docs.spring.io/spring-security/reference/servlet/oauth2/index.html](https://docs.spring.io/spring-security/reference/servlet/oauth2/index.html)
- [WebFlux - https://docs.spring.io/spring-security/reference/reactive/oauth2/index.html](https://docs.spring.io/spring-security/reference/reactive/oauth2/index.html)

### Apache HTTPd
- [https://httpd.apache.org/](https://httpd.apache.org/)
- [https://github.com/OpenIDC/mod_auth_openidc](https://github.com/OpenIDC/mod_auth_openidc)

### NGINX
- [https://www.nginx.com/](https://www.nginx.com/)
- [https://openresty.org/en/](https://openresty.org/en/)
- [https://github.com/zmartzone/lua-resty-openidc](https://github.com/zmartzone/lua-resty-openidc)

### PHP
- [https://www.php.net/](https://www.php.net/)
- [https://github.com/jumbojett/OpenID-Connect-PHP](https://github.com/jumbojett/OpenID-Connect-PHP)

### Python
- [https://www.python.org/](https://www.python.org/)
- [https://github.com/mozilla/mozilla-django-oidc](https://github.com/mozilla/mozilla-django-oidc)

### Java EE
- [https://www.payara.fish/](https://www.payara.fish/)

### JavaScript / TypeScript
- [https://www.npmjs.com/package/oidc-client](https://www.npmjs.com/package/oidc-client)
- [https://github.com/authts/oidc-client-ts](https://github.com/authts/oidc-client-ts)

