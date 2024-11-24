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
