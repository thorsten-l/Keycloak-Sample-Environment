<?php

namespace App;

class OidcDiscovery
{
    private string $issuer;
    private string $authorizationEndpoint;
    private string $tokenEndpoint;
    private ?string $endSessionEndpoint;
    private ?string $userinfoEndpoint;

    public function __construct(array $data)
    {
        $this->issuer = $data['issuer'];
        $this->authorizationEndpoint = $data['authorization_endpoint'];
        $this->tokenEndpoint = $data['token_endpoint'];
        $this->endSessionEndpoint = $data['end_session_endpoint'] ?? null;
        $this->userinfoEndpoint = $data['userinfo_endpoint'] ?? null;
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