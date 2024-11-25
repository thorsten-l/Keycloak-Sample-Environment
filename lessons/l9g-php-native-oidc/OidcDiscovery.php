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
 * This file defines the OidcDiscovery class, which represents the OIDC discovery endpoints.
 * It includes methods to get the issuer, authorization endpoint, token endpoint,
 * end session endpoint, and userinfo endpoint.
 */

namespace App;

class OidcDiscovery
{
    private string $issuer;
    private string $authorizationEndpoint;
    private string $tokenEndpoint;
    private string $endSessionEndpoint;
    private string $userinfoEndpoint;

    public function __construct(array $data)
    {
        $this->issuer = $data['issuer'];
        $this->authorizationEndpoint = $data['authorization_endpoint'];
        $this->tokenEndpoint = $data['token_endpoint'];
        $this->endSessionEndpoint = $data['end_session_endpoint'];
        $this->userinfoEndpoint = $data['userinfo_endpoint'];
    }

    public function getIssuer(): string
    {
        return $this->issuer;
    }

    public function getAuthorizationEndpoint(): string
    {
        return $this->authorizationEndpoint;
    }

    public function getTokenEndpoint(): string
    {
        return $this->tokenEndpoint;
    }

    public function getEndSessionEndpoint(): ?string
    {
        return $this->endSessionEndpoint;
    }

    public function getUserinfoEndpoint(): ?string
    {
        return $this->userinfoEndpoint;
    }
}
