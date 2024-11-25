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

error_log("/index.php called");

session_start();

if (isset($_SESSION['tokens'])) {
  header('Location: /app');
  exit();
}

$oidc = new OidcService($config);
$state = bin2hex(random_bytes(16));

$code_verifier = $oidc->generateCodeVerifier();
$code_challenge = $oidc->generateCodeChallenge($code_verifier);

$_SESSION['oauth2_state'] = $state;
$_SESSION['code_verifier'] = $code_verifier;

$authUrl = $oidc->buildAuthorizationUrl($state, $code_challenge);
?>
<!DOCTYPE html>
<html>

<head>
  <title>Home</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="main.css" rel="stylesheet">
</head>

<body>
  <div class="container mt-4">
    <h1>Home</h1>

    <nav class="appheader">
      <div class="nav nav-tabs" id="nav-tab" role="tablist">
        <a class="nav-link active" id="nav-login-url-tab" data-bs-toggle="tab" href="#nav-login-url" role="tab" aria-controls="nav-login-url" aria-selected="true">Login-URL</a>
        <a class="nav-link" id="nav-session-tab" data-bs-toggle="tab" href="#nav-session" role="tab" aria-controls="nav-session" aria-selected="false">Session Attributes</a>
      </div>
    </nav>

    <div class="tab-content" id="nav-tabContent">
      <div class="tab-pane fade show active" id="nav-login-url" role="tabpanel" aria-labelledby="nav-login-url-tab">
        <div class="appheader card">
          <div class="card-header">
            <div class="d-flex justify-content-between align-items-center">
              <h4>Login URL</h4>
              <a class="btn btn-primary" href="<?php echo $authUrl; ?>">LOGIN</a>
            </div>
          </div>
          <div class="card-body">
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
                  <td><?php echo htmlspecialchars($config['oidc_discovery']['authorization_endpoint']); ?></td>
                </tr>
                <tr>
                  <td>client_id</td>
                  <td><?php echo htmlspecialchars($config['client_id']); ?></td>
                </tr>
                <tr>
                  <td>response_type</td>
                  <td>code</td>
                </tr>
                <tr>
                  <td>redirect_uri</td>
                  <td><?php echo htmlspecialchars($config['redirect_uri']); ?></td>
                </tr>
                <tr>
                  <td>scope</td>
                  <td><?php echo htmlspecialchars($config['scope']); ?></td>
                </tr>
                <tr>
                  <td>state</td>
                  <td><?php echo htmlspecialchars($state); ?></td>
                </tr>
                <tr>
                  <td class="red-background">code_challenge</td>
                  <td class="red-background"><?php echo htmlspecialchars($code_challenge); ?></td>
                </tr>
                <tr>
                  <td class="red-background">code_challenge_method</td>
                  <td class="red-background">S256</td>
                </tr>
              </tbody>
            </table>

            <p class="red-background p-1">*** Optional: PKCE code challenge ***</p>

          </div>
        </div>
      </div>
      <div class="tab-pane fade show" id="nav-session" role="tabpanel" aria-labelledby="nav-session-tab">
        <div class="appheader card">
          <div class="card-header">
            <h4>Session Attributes</h4>
          </div>
          <div class="card-body">
            <table class="table table-striped">
              <thead>
                <tr>
                  <th style="width: 20%">Key</th>
                  <th style="width: 80%">Value</th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td>session id</td>
                  <td><?php echo session_id(); ?></td>
                </tr>
                <tr>
                  <td>oauth2State</td>
                  <td><?php echo htmlspecialchars($_SESSION['oauth2_state']); ?></td>
                </tr>
                <tr>
                  <td>code_verifier</td>
                  <td><?php echo htmlspecialchars($_SESSION['code_verifier']); ?></td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>

  </div>
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>

</html>