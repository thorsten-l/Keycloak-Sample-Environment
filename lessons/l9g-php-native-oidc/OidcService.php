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
 * This file defines the OidcService class, which handles various OIDC-related operations
 * such as generating the authorization URL, exchanging authorization codes for tokens,
 * decoding JWT tokens, and constructing the logout URL.
 */

namespace App;

class OidcService
{
  private $config;

  public function __construct($config)
  {
    $this->config = $config;
  }

  public function buildAuthorizationUrl($state, $code_challenge): string
  {
    return $this->config['oidc_discovery']['authorization_endpoint'] . '?' . http_build_query([
      'response_type' => 'code',
      'client_id' => $this->config['client_id'],
      'redirect_uri' => $this->config['redirect_uri'],
      'scope' => $this->config['scope'],
      'state' => $state,
      'code_challenge' => $code_challenge,
      'code_challenge_method' => 'S256',
    ]);
  }

  public function decodeJwt($token)
  {
    $parts = explode('.', $token);
    return json_decode(base64_decode($parts[1]), true);
  }

  public function fetchOAuth2Tokens(string $code, string $code_verifier): array
  {
    $tokenEndpoint = $this->config['oidc_discovery']['token_endpoint'];

    $postData = [
      'grant_type' => 'authorization_code',
      'code' => $code,
      'code_verifier' => $code_verifier,
      'redirect_uri' => $this->config['redirect_uri'],
      'client_id' => $this->config['client_id'],
      'client_secret' => $this->config['client_secret'],
    ];

    $response = $this->makePostRequest($tokenEndpoint, $postData);

    if (!$response) {
      throw new \Exception('Failed to exchange authorization code for tokens.');
    }

    $responseData = json_decode($response, true);

    if (!isset($responseData['access_token'])) {
      throw new \Exception('Invalid token response: ' . $response);
    }

    return $responseData;
  }

  private function makePostRequest(string $url, array $data): string
  {
    $ch = curl_init();

    curl_setopt($ch, CURLOPT_URL, $url);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
    curl_setopt($ch, CURLOPT_POST, true);
    curl_setopt($ch, CURLOPT_POSTFIELDS, http_build_query($data));
    curl_setopt($ch, CURLOPT_HTTPHEADER, [
      'Content-Type: application/x-www-form-urlencoded',
    ]);

    $response = curl_exec($ch);

    if (curl_errno($ch)) {
      throw new \Exception('cURL error: ' . curl_error($ch));
    }

    curl_close($ch);

    return $response;
  }

  public function buildLogoutUrl(string $idToken, string $postLogoutRedirectUri): string
  {
    if (empty($this->config['oidc_discovery']['end_session_endpoint'])) {
      throw new \Exception('End Session Endpoint is not defined in OIDC Discovery.');
    }

    $logoutEndpoint = $this->config['oidc_discovery']['end_session_endpoint'];

    $queryParams = http_build_query([
      'id_token_hint' => $idToken,
      'post_logout_redirect_uri' => $postLogoutRedirectUri,
    ]);

    return $logoutEndpoint . '?' . $queryParams;
  }

  public function generateCodeVerifier(): string
  {
    $randomBytes = random_bytes(32);
    return rtrim(strtr(base64_encode($randomBytes), '+/', '-_'), '=');
  }

  public function generateCodeChallenge(string $codeVerifier): string
  {
    $hash = hash('sha256', $codeVerifier, true);
    return rtrim(strtr(base64_encode($hash), '+/', '-_'), '=');
  }
}
