<?php

namespace App;

class OidcService {
    private $config;

    public function __construct($config) {
        $this->config = $config;
    }

    public function getAuthorizationUrl($state) {
        return $this->config['oidc_discovery']['authorization_endpoint'] . '?' . http_build_query([
            'response_type' => 'code',
            'client_id' => $this->config['client_id'],
            'redirect_uri' => $this->config['redirect_uri'],
            'scope' => $this->config['scopes'],
            'state' => $state,
        ]);
    }

    public function decodeJwt($token) {
        $parts = explode('.', $token);
        return json_decode(base64_decode($parts[1]), true);
    }

    public function exchangeCodeForTokens(string $code): array
    {
        $tokenEndpoint = $this->config['oidc_discovery']['token_endpoint'];

        $postData = [
            'grant_type' => 'authorization_code',
            'code' => $code,
            'redirect_uri' => $this->config['redirect_uri'],
            'client_id' => $this->config['client_id'],
            'client_secret' => $this->config['client_secret'],
        ];

        $response = $this->makePostRequest($tokenEndpoint, $postData);

        if (!$response) {
            throw new Exception('Failed to exchange authorization code for tokens.');
        }

        $responseData = json_decode($response, true);

        if (!isset($responseData['access_token'])) {
            throw new Exception('Invalid token response: ' . $response);
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
            throw new Exception('cURL error: ' . curl_error($ch));
        }

        curl_close($ch);

        return $response;
    }

  public function getLogoutUrl(string $idToken, string $postLogoutRedirectUri): string
  {
    if (empty($this->config['oidc_discovery']['end_session_endpoint'])) {
        throw new Exception('End Session Endpoint is not defined in OIDC Discovery.');
    }

    $logoutEndpoint = $this->config['oidc_discovery']['end_session_endpoint'];

    $queryParams = http_build_query([
        'id_token_hint' => $idToken,
        'post_logout_redirect_uri' => $postLogoutRedirectUri,
    ]);

    return $logoutEndpoint . '?' . $queryParams;
  }
}
