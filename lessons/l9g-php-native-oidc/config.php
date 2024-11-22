<?php

$config = [
    'client_id' => 'app1',
    'client_secret' => 'x45mpvfzvPU5utH7MvNclj2vtilaXW0i',
    'redirect_uri' => 'http://app1.dev.sonia.de:8081/oidc-login.php',
    'scopes' => 'openid profile email roles',
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
