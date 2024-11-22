<?php

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