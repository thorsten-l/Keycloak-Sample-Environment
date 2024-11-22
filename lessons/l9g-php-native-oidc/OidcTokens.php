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
 * This file defines the OidcTokens class, which represents the tokens received from the OIDC provider.
 * It includes methods to get the access token, ID token, refresh token, token type, and expiration time.
 */

namespace App;

class OidcTokens
{
    private string $accessToken;
    private string $idToken;
    private ?string $refreshToken;
    private string $tokenType;
    private int $expiresIn;

    public function __construct(array $data)
    {
        $this->accessToken = $data['access_token'];
        $this->idToken = $data['id_token'];
        $this->refreshToken = $data['refresh_token'] ?? null;
        $this->tokenType = $data['token_type'];
        $this->expiresIn = $data['expires_in'];
    }

    public function getAccessToken(): string
    {
        return $this->accessToken;
    }

    public function getIdToken(): string
    {
        return $this->idToken;
    }

    public function getRefreshToken(): ?string
    {
        return $this->refreshToken;
    }

    public function getTokenType(): string
    {
        return $this->tokenType;
    }

    public function getExpiresIn(): int
    {
        return $this->expiresIn;
    }
}