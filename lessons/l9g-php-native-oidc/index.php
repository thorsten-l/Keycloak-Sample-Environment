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
        <a class="nav-link" id="nav-discovery-tab" data-bs-toggle="tab" href="#nav-discovery" role="tab" aria-controls="nav-discovery" aria-selected="false">OIDC Discovery</a>
      </div>
    </nav>

    <div class="tab-content" id="nav-tabContent">
      <div class="tab-pane fade show active" id="nav-login-url" role="tabpanel" aria-labelledby="nav-login-url-tab">
        <div class="appheader card">
          <div class="card-header">
            <div class="d-flex justify-content-between align-items-center">
              <div class="appcard-title">Login URL</div>
              <a class="btn btn-primary" href="<?php echo $authUrl; ?>">LOGIN</a>
            </div>
          </div>
          <div class="card-body">
            <table class="table table-striped">
              <thead>
                <tr>
                  <th style="width: 10%">Key</th>
                  <th style="width: 40%">Value</th>
                  <th style="width: 50%">Description</th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td class="discovery-background">uri</td>
                  <td class="discovery-background"><?php echo htmlspecialchars($config['oidc_discovery']['authorization_endpoint']); ?></td>
                  <td class="discovery-background"><?php echo $descriptions['authorization_endpoint'] ?></td>
                </tr>
                <tr>
                  <td class="config-background">client_id</td>
                  <td class="config-background"><?php echo htmlspecialchars($config['client_id']); ?></td>
                  <td class="config-background"><?php echo $descriptions['client_id'] ?></td>
                </tr>
                <tr>
                  <td class="config-background">response_type</td>
                  <td class="config-background">code</td>
                  <td class="config-background"><?php echo $descriptions['response_type'] ?></td>
                </tr>
                <tr>
                  <td class="config-background">redirect_uri</td>
                  <td class="config-background"><?php echo htmlspecialchars($config['redirect_uri']); ?></td>
                  <td class="config-background"><?php echo $descriptions['redirect_uri'] ?></td>
                </tr>
                <tr>
                  <td class="config-background">scope</td>
                  <td class="config-background"><?php echo htmlspecialchars($config['scope']); ?></td>
                  <td class="config-background"><?php echo $descriptions['scope'] ?></td>
                </tr>
                <tr>
                  <td class="random-background">state</td>
                  <td class="random-background"><?php echo htmlspecialchars($state); ?></td>
                  <td class="random-background"><?php echo $descriptions['state'] ?></td>
                </tr>
                <tr>
                  <td class="optional-background">code_challenge</td>
                  <td class="random-background"><?php echo htmlspecialchars($code_challenge); ?></td>
                  <td class="random-background"><?php echo $descriptions['code_challenge'] ?></td>
                </tr>
                <tr>
                  <td class="optional-background">code_challenge_method</td>
                  <td class="config-background">S256</td>
                  <td class="config-background"><?php echo $descriptions['code_challenge_method'] ?></td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

      </div>
      <div class="tab-pane fade show" id="nav-session" role="tabpanel" aria-labelledby="nav-session-tab">
        <div class="appheader card">
          <div class="card-header">
            <div class="appcard-title">Session Attributes</div>
          </div>
          <div class="card-body">
            <table class="table table-striped">
              <thead>
                <tr>
                  <th style="width: 10%">Key</th>
                  <th style="width: 40%">Value</th>
                  <th style="width: 50%">Description</th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td class="random-background">session id</td>
                  <td class="random-background"><?php echo session_id(); ?></td>
                  <td class="random-background"><?php echo $descriptions['session_id'] ?></td>
                </tr>
                <tr>
                  <td class="random-background">oauth2_state</td>
                  <td class="random-background"><?php echo htmlspecialchars($_SESSION['oauth2_state']); ?></td>
                  <td class="random-background"><?php echo $descriptions['state'] ?></td>
                </tr>
                <tr>
                  <td class="random-background">code_verifier</td>
                  <td class="random-background"><?php echo htmlspecialchars($_SESSION['code_verifier']); ?></td>
                  <td class="random-background"><?php echo $descriptions['code_verifier'] ?></td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
      <div class="tab-pane fade show" id="nav-discovery" role="tabpanel" aria-labelledby="nav-discovery-tab">
        <div class="appheader card">
          <div class="card-header">
            <div class="appcard-title">OIDC Discovery</div>
          </div>
          <div class="card-body">
            <table class="table table-striped">
              <thead>
                <tr>
                  <th style="width: 10%">Key</th>
                  <th style="width: 40%">Value</th>
                  <th style="width: 50%">Description</th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td class="config-background">oidc_discovery_url</td>
                  <td class="config-background"><?php echo htmlspecialchars($config['oidc_discovery_url']); ?></td>
                  <td class="config-background"><?php echo $descriptions['oidc_discovery_url'] ?></td>
                </tr>
                <tr>
                  <td class="discovery-background">issuer</td>
                  <td class="discovery-background"><?php echo htmlspecialchars($config['oidc_discovery']['issuer']); ?></td>
                  <td class="discovery-background"><?php echo $descriptions['issuer'] ?></td>
                </tr>
                <tr>
                  <td class="discovery-background">authorization_endpoint</td>
                  <td class="discovery-background"><?php echo htmlspecialchars($config['oidc_discovery']['authorization_endpoint']); ?></td>
                  <td class="discovery-background"><?php echo $descriptions['authorization_endpoint'] ?></td>
                </tr>
                <tr>
                  <td class="discovery-background">token_endpoint</td>
                  <td class="discovery-background"><?php echo htmlspecialchars($config['oidc_discovery']['token_endpoint']); ?></td>
                  <td class="discovery-background"><?php echo $descriptions['token_endpoint'] ?></td>
                </tr>
                <tr>
                  <td class="discovery-background">end_session_endpoint</td>
                  <td class="discovery-background"><?php echo htmlspecialchars($config['oidc_discovery']['end_session_endpoint']); ?></td>
                  <td class="discovery-background"><?php echo $descriptions['end_session_endpoint'] ?></td>
                </tr>
                <tr>
                  <td class="discovery-background">userinfo_endpoint</td>
                  <td class="discovery-background"><?php echo htmlspecialchars($config['oidc_discovery']['userinfo_endpoint']); ?></td>
                  <td class="discovery-background"><?php echo $descriptions['userinfo_endpoint'] ?></td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>

    <div class="row mt-4">
      <div class="col text-center">
        <p class="config-background p-1">Config</p>
      </div>
      <div class="col text-center">
        <p class="discovery-background p-1">Discovery</p>
      </div>
      <div class="col text-center">
        <p class="random-background p-1">Random</p>
      </div>
      <div class="col text-center">
        <p class="optional-background p-1">Optional</p>
      </div>
    </div>

  </div>
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>

</html>