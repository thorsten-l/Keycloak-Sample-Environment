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
  header('Location: /');
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
  if (is_numeric($value) && $value > 1700000000) {
    return date('Y-m-d H:i:s', $value);
  }
  return $value;
}
?>
<!DOCTYPE html>
<html>

<head>
  <title>App</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" />
  <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.7.1/css/all.min.css" rel="stylesheet" />
  <link href="/css/main.css" rel="stylesheet" />
</head>

<body>
  <div class="container mt-4">
    <h1>App</h1>

    <nav class="appheader">
      <div class="nav nav-tabs" id="nav-tab" role="tablist">
        <a class="nav-link active" id="nav-logout-url-tab" data-bs-toggle="tab" href="#nav-logout-url" role="tab" aria-controls="nav-logout-url" aria-selected="true">Logout-URL</a>
        <a class="nav-link" id="nav-userinfo-tab" data-bs-toggle="tab" href="#nav-userinfo" role="tab" aria-controls="nav-userinfo" aria-selected="false">Userinfo</a>
        <a class="nav-link" id="nav-idtoken-tab" data-bs-toggle="tab" href="#nav-idtoken" role="tab" aria-controls="nav-idtoken" aria-selected="false">ID Token</a>
        <a class="nav-link" id="nav-accesstoken-tab" data-bs-toggle="tab" href="#nav-accesstoken" role="tab" aria-controls="nav-accesstoken" aria-selected="false">Access Token</a>
        <a class="nav-link" id="nav-refreshtoken-tab" data-bs-toggle="tab" href="#nav-refreshtoken" role="tab" aria-controls="nav-refreshtoken" aria-selected="false">Refresh Token</a>
      </div>
    </nav>

    <div class="tab-content" id="nav-tabContent">
      <div class="tab-pane fade show active" id="nav-logout-url" role="tabpanel" aria-labelledby="nav-logout-url-tab">
        <div class="card appheader">
          <div class="card-header">
            <div class="d-flex justify-content-between align-items-center">
              <div class="appcard-title">Logout URL</div>
              <a class="btn btn-primary" href="<?php echo htmlspecialchars($logoutUrl); ?>">LOGOUT</a>
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
                  <td class="discovery-background">uri</td>
                  <td class="discovery-background"><?php echo htmlspecialchars($config['oidc_discovery']['end_session_endpoint']); ?></td>
                </tr>
                <tr>
                  <td>id_token_hint
                    <div class="mt-2">                    
                      <button class="btn btn-secondary" onclick="copyToClipboard('<?php echo htmlspecialchars($idToken); ?>')"><i class="fa-regular fa-copy"></i></button>
                    </div>
                  </td>
                  <td style="word-break: break-all;">
                    <?php echo htmlspecialchars($idToken); ?>
                  </td>
                </tr>
                <tr>
                  <td class="optional-background">post_logout_redirect_uri</td>
                  <td class="config-background"><?php echo htmlspecialchars($postLogoutRedirectUri); ?></td>
                </tr>
                <tr>
                  <td>Copy ID Token</td>
                  <td><a target="_blank" title="View on JWT.io" href="https://jwt.io"><img height="48" src="images/jwt.io-badge.svg"></a></td>
                </tr>
                <tr>
                  <td>PHP-Libraries</td>
                  <td>            
                    <a class="ms-2" target="_blank" title="JWT PHP Libraries" href="https://jwt.io/libraries?language=PHP"><img height="48" src="images/jwt.io-logo-asset.svg"></a>
                    <span class="ms-2">composer require web-token/jwt-framework</span>
                  </td>
                </tr>
              </tbody>
            </table>

            

          </div>
        </div>
      </div>

      <div class="tab-pane fade show" id="nav-userinfo" role="tabpanel" aria-labelledby="nav-userinfo-tab">
        <div class="card appheader">
          <div class="card-header">
            <div class="d-flex justify-content-between align-items-center">
              <div class="appcard-title">Userinfo</div>
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
                  <td>username</td>
                  <td><?php echo htmlspecialchars($idTokenData[$config['attributesMap']['username']]); ?></td>
                </tr>
                <tr>
                  <td>email</td>
                  <td><?php echo htmlspecialchars($idTokenData[$config['attributesMap']['email']]); ?></td>
                </tr>
                <tr>
                  <td>family_name</td>
                  <td><?php echo htmlspecialchars($idTokenData[$config['attributesMap']['family_name']]); ?></td>
                </tr>
                <tr>
                  <td>given_name</td>
                  <td><?php echo htmlspecialchars($idTokenData[$config['attributesMap']['given_name']]); ?></td>
                </tr>
                <tr>
                  <td>gender</td>
                  <td><?php echo htmlspecialchars($idTokenData['gender']); ?></td>
                </tr>
                <tr>
                  <td>realm roles</td>
                  <td><?php echo convertToString($accessTokenData['realm_access']['roles']); ?></td>
                </tr>
                <tr>
                  <td>resource roles</td>
                  <td><?php echo convertToString($accessTokenData['resource_access'][$config['client_id']]['roles']); ?></td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>

      <div class="tab-pane fade show" id="nav-idtoken" role="tabpanel" aria-labelledby="nav-idtoken-tab">
        <div class="card appheader">
          <div class="card-header">
            <div class="appcard-title">ID Token</div>
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
                <?php foreach ($idTokenData as $key => $value): ?>
                  <tr class="align-middle">
                    <td><?php echo $key ?></td>
                    <td><?php echo htmlspecialchars(convertToString($value)) ?></td>
                    <td><?php echo $descriptions[$key] ?></td>
                  </tr>
                <?php endforeach; ?>
              </tbody>
            </table>
          </div>
        </div>
      </div>

      <div class="tab-pane fade show" id="nav-accesstoken" role="tabpanel" aria-labelledby="nav-accesstoken-tab">
        <div class="card appheader">
          <div class="card-header">
            <div class="appcard-title">Access Token</div>
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
                <?php foreach ($accessTokenData as $key => $value): ?>
                  <tr class="align-middle">
                    <td><?php echo $key ?></td>
                    <td><?php echo htmlspecialchars(convertToString($value)) ?></td>
                    <td><?php echo $descriptions[$key] ?></td>
                  </tr>
                <?php endforeach; ?>
              </tbody>
            </table>
          </div>
        </div>
      </div>

      <div class="tab-pane fade show" id="nav-refreshtoken" role="tabpanel" aria-labelledby="nav-refreshtoken-tab">
        <div class="card appheader">
          <div class="card-header">
            <div class="appcard-title">Refresh Token</div>
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
                <?php foreach ($refreshTokenData as $key => $value): ?>
                  <tr class="align-middle">
                    <td><?php echo $key ?></td>
                    <td><?php echo htmlspecialchars(convertToString($value)) ?></td>
                    <td><?php echo $descriptions[$key] ?></td>
                  </tr>
                <?php endforeach; ?>
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

  <script>
    function copyToClipboard(text) {
      const tempInput = document.createElement("textarea");
      tempInput.value = text;
      document.body.appendChild(tempInput);
      tempInput.select();
      document.execCommand("copy");
      document.body.removeChild(tempInput);
    }
  </script>
</body>

</html>