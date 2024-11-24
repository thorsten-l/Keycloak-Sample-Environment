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

error_log("/app.php called");

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
$postLogoutRedirectUri = $config['post_logout_redirect_uri'];

$logoutUrl = $oidc->buildLogoutUrl($idToken, $postLogoutRedirectUri);

function convertToString($value)
{
    if (is_array($value)) {
        return json_encode($value);
    }
    return $value;
}
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

        <p style="word-break: break-all;"><?php echo htmlspecialchars($logoutUrl); ?></p>

        <h2 class="appheader">Logout URL</h2>
        <table class="table table-striped">
            <thead>
                <tr>
                    <th style="width: 20%">Key</th>
                    <th style="width: 80%">Value</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td>uri</td>
                    <td><?php echo htmlspecialchars($config['oidc_discovery']['end_session_endpoint']); ?></td>
                </tr>
                <tr>
                    <td>id_token_hint</td>
                    <td style="word-break: break-all;"><?php echo htmlspecialchars($idToken); ?></td>
                </tr>
                <tr>
                    <td class="red-background">post_logout_redirect_uri</td>
                    <td class="red-background"><?php echo htmlspecialchars($postLogoutRedirectUri); ?></td>
                </tr>
            </tbody>
        </table>


        <h2 class="appheader">ID Token</h2>
        <table class="table table-striped">
            <thead>
                <tr>
                    <th style="width: 20%">Key</th>
                    <th style="width: 80%">Value</th>
                </tr>
            </thead>
            <tbody>
                <?php foreach ($idTokenData as $key => $value): ?>
                    <tr class="align-middle">
                        <td><?php echo $key ?></td>
                        <td><?php echo htmlspecialchars(convertToString($value)) ?></td>
                    </tr>
                <?php endforeach; ?>
            </tbody>
        </table>

        <h2 class="appheader">Access Token</h2>
        <table class="table table-striped">
            <thead>
                <tr>
                    <th style="width: 20%">Key</th>
                    <th style="width: 80%">Value</th>
                </tr>
            </thead>
            <tbody>
                <?php foreach ($accessTokenData as $key => $value): ?>
                    <tr class="align-middle">
                        <td><?php echo $key ?></td>
                        <td><?php echo htmlspecialchars(convertToString($value)) ?></td>
                    </tr>
                <?php endforeach; ?>
            </tbody>
        </table>

        <h2 class="appheader">Refresh Token</h2>
        <table class="table table-striped">
            <thead>
                <tr>
                    <th style="width: 20%">Key</th>
                    <th style="width: 80%">Value</th>
                </tr>
            </thead>
            <tbody>
                <?php foreach ($refreshTokenData as $key => $value): ?>
                    <tr class="align-middle">
                        <td><?php echo $key ?></td>
                        <td><?php echo htmlspecialchars(convertToString($value)) ?></td>
                    </tr>
                <?php endforeach; ?>
            </tbody>
        </table>

    </div>
</body>

</html>