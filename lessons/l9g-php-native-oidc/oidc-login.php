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
 * This file is the entry point of the application.
 * It initializes the OIDC service, generates a state parameter,
 * and constructs the authorization URL for the user to log in.
 */

require_once 'config.php';
require_once 'OidcService.php';

use App\OidcService;

session_start();

if (isset($_GET['code']) && isset($_GET['state'])) {
    $code = $_GET['code'];
    $state = $_GET['state'];

    if ($state !== $_SESSION['oauth2_state']) {
        die('Invalid state');
    }

    $oidc = new OidcService($config);
    $tokens = $oidc->exchangeCodeForTokens($code);

    error_log(print_r($tokens, true));

    $_SESSION['tokens'] = $tokens;
    header('Location: app.php');
    exit();
} else {
    die('Missing code or state');
}
