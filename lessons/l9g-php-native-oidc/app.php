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
 * This file handles the main application logic after the user has logged in.
 * It decodes the JWT tokens and displays the token data. It also provides a logout URL.
 */

require_once 'config.php';
require_once 'OidcService.php';

use App\OidcService;

session_start();
if (!isset($_SESSION['tokens'])) {
    header('Location: index.php');
    exit();
}

$tokens = $_SESSION['tokens'];
$oidc = new OidcService($config);
$idTokenData = $oidc->decodeJwt($tokens['id_token']);
$accessTokenData = $oidc->decodeJwt($tokens['access_token']);
$refreshTokenData = $oidc->decodeJwt($tokens['refresh_token']);

$idToken = $tokens['id_token'];
$postLogoutRedirectUri = 'http://app1.dev.sonia.de:8081/';

$logoutUrl = $oidc->getLogoutUrl($idToken, $postLogoutRedirectUri);

?>
<!DOCTYPE html>
<html>
<head>
    <title>App</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
    <div class="container mt-4">
        <h1>App</h1>
        <a class="btn btn-primary mt-4" href="<?php echo htmlspecialchars($logoutUrl); ?>">LOGOUT</a>
        <h2 class="mt-4">ID Token</h2>
        <pre><?php print_r($idTokenData); ?></pre>
        <h2 class="mt-4">Access Token</h2>
        <pre><?php print_r($accessTokenData); ?></pre>
        <h2 class="mt-4">Refresh Token</h2>
        <pre><?php print_r($refreshTokenData); ?></pre>
    </div>
</body>
</html>
