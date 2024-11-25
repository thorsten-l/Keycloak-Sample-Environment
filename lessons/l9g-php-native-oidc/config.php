<?php
/*
 * Copyright 2024 Thorsten Ludewig (t.ludewig@gmail.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * This file contains the configuration settings for the OIDC client,
 * including client ID, client secret, redirect URI, scopes, and the OIDC discovery URL.
 * It fetches the OIDC discovery endpoints and stores them in the configuration array.
 */

$config = [
    'redis' => [
        'host' => 'app1-redis',
        'port' => 6379,
    ],
    'client_id' => 'app1',
    'client_secret' => 'x45mpvfzvPU5utH7MvNclj2vtilaXW0i',
    'redirect_uri' => 'http://app1.dev.sonia.de:8081/oidc-login',
    'post_logout_redirect_uri' => 'http://app1.dev.sonia.de:8081/oidc-logout',
    'scope' => 'openid profile email roles',
    'oidc_discovery_url' => 'https://id.dev.sonia.de/realms/dev/.well-known/openid-configuration',
];

// Fetch OIDC discovery endpoints
$response = file_get_contents($config['oidc_discovery_url']);
$oidcDiscovery = json_decode($response, true);

$config['oidc_discovery'] = [
    'issuer' => $oidcDiscovery['issuer'],
    'authorization_endpoint' => $oidcDiscovery['authorization_endpoint'],
    'token_endpoint' => $oidcDiscovery['token_endpoint'],
    'end_session_endpoint' => $oidcDiscovery['end_session_endpoint'] ?? null,
    'userinfo_endpoint' => $oidcDiscovery['userinfo_endpoint'] ?? null,
];

$tokenDescriptions = [
    "exp" => "Das Ablaufdatum des Tokens als UNIX-Timestamp. Nach diesem Zeitpunkt ist das Token ungültig.",
    "iat" => "Der Zeitpunkt der Erstellung des Tokens als UNIX-Timestamp.",
    "auth_time" => "Der Zeitpunkt, an dem der Benutzer authentifiziert wurde, als UNIX-Timestamp.",
    "jti" => "Die eindeutige ID des Tokens, die zur Verfolgung oder Entwertung verwendet werden kann.",
    "iss" => "Der Aussteller des Tokens, typischerweise die URL des Identity Providers.",
    "aud" => "Der beabsichtigte Empfänger des Tokens, typischerweise die Client-ID der Anwendung.",
    "sub" => "Der eindeutige Identifier des Benutzers, der das Token betrifft.",
    "typ" => "Der Typ des Tokens, z. B. 'ID' für ID-Tokens.",
    "azp" => "Der autorisierte Client, der das Token anfordert.",
    "sid" => "Die Sitzungs-ID, die die aktuelle Anmeldesitzung des Benutzers identifiziert.",
    "at_hash" => "Ein Hash des Access Tokens, um es mit dem ID Token zu verifizieren.",
    "acr" => "Die Authentifizierungsstärke oder Methode, die während der Authentifizierung verwendet wurde.",
    "resource_access" => "Eine Liste der Rollen und Berechtigungen, die der Benutzer für verschiedene Ressourcen besitzt.",
    "realm_access" => "Die Rollen und Berechtigungen, die der Benutzer innerhalb des aktuellen Realms hat.",
    "email_verified" => "Ein Indikator (0 oder 1), ob die E-Mail-Adresse des Benutzers verifiziert wurde.",
    "gender" => "Das Geschlecht des Benutzers, falls angegeben.",
    "name" => "Der vollständige Name des Benutzers.",
    "nickname" => "Der Spitzname des Benutzers.",
    "phone_number" => "Die Telefonnummer des Benutzers.",
    "preferred_username" => "Der bevorzugte Benutzername des Benutzers.",
    "locale" => "Die bevorzugte Sprache und Region des Benutzers, z. B. 'de' für Deutsch.",
    "given_name" => "Der Vorname des Benutzers.",
    "family_name" => "Der Nachname des Benutzers.",
    "allowed-origins" => "Eine Liste von erlaubten Ursprüngen (Origins), die CORS-Anfragen für diesen Benutzer oder Client autorisieren.",
    "email" => "Die E-Mail-Adresse des Benutzers.",
    "scope" => "Die genehmigten Berechtigungen und Zugriffsebenen für die Ressourcen, auf die das Token Zugriff gewährt."
];

date_default_timezone_set('Europe/Berlin');
