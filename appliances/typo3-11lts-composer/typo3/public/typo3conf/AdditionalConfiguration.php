<?php
$GLOBALS['TYPO3_CONF_VARS']['BE']['cookieSameSite'] = 'lax';
$GLOBALS['TYPO3_CONF_VARS']['FE']['cookieSameSite'] = 'lax';

$GLOBALS['TYPO3_CONF_VARS']['EXTENSIONS']['oauth2_client'] = [
    'providers' => [
        'keycloak' => [
            'label' => 'ID.DEV.SONIA.DE',
            'description' => 'Login with ID.DEV.SONIA.DE',
            'scopes' => [
                \Waldhacker\Oauth2Client\Service\Oauth2ProviderManager::SCOPE_FRONTEND,
                \Waldhacker\Oauth2Client\Service\Oauth2ProviderManager::SCOPE_BACKEND,
            ],
            'options' => [
                'clientId' => 'typo3',
                'clientSecret' => '524ZaIps9LEuGget22d8fszSCxoKGaXV',
                'urlAuthorize' => 'https://id.dev.sonia.de/realms/dev/protocol/openid-connect/auth',
                'urlAccessToken' => 'https://id.dev.sonia.de/realms/dev/protocol/openid-connect/token',
                'urlResourceOwnerDetails' => 'https://id.dev.sonia.de/realms/dev/protocol/openid-connect/userinfo',
                'responseResourceOwnerId' => 'eduPersonUniqueId',
                'scopes' => ['openid', 'profile', 'email'],
                'scopeSeparator' => ' '
            ],
        ],
    ],
];
