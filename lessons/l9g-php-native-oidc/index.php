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

require_once 'config.php';
require_once 'OidcService.php';

use App\OidcService;

session_start();
$oidc = new OidcService($config);

$state = bin2hex(random_bytes(16));
$_SESSION['oauth2_state'] = $state;

$authUrl = $oidc->getAuthorizationUrl($state);
?>
<!DOCTYPE html>
<html>
<head>
    <title>Home</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
    <div class="container mt-4">
        <h1>Home</h1>
        <a class="btn btn-primary mt-4" href="<?php echo $authUrl; ?>">LOGIN</a>
    </div>
</body>
</html>