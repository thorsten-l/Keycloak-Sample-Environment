<?php
return [
    'BE' => [
        'cookieSameSite' => 'lax',
        'debug' => true,
        'explicitADmode' => 'explicitAllow',
        'installToolPassword' => '$argon2i$v=19$m=65536,t=16,p=1$VmlpemV5NEdQdy5pWnkyag$jEbsEX5rCaa8f/M8H1NUaGBXBh+nrSiw9cIpUuDnjfw',
        'loginRateLimit' => 0,
        'passwordHashing' => [
            'className' => 'TYPO3\\CMS\\Core\\Crypto\\PasswordHashing\\Argon2iPasswordHash',
            'options' => [],
        ],
    ],
    'DB' => [
        'Connections' => [
            'Default' => [
                'charset' => 'utf8mb4',
                'dbname' => 'typo3db',
                'driver' => 'mysqli',
                'host' => 'typo3db',
                'password' => 'typo3db',
                'port' => 3306,
                'tableoptions' => [
                    'charset' => 'utf8mb4',
                    'collate' => 'utf8mb4_unicode_ci',
                ],
                'user' => 'typo3db',
            ],
        ],
    ],
    'EXTENSIONS' => [
        'backend' => [
            'backendFavicon' => '',
            'backendLogo' => '',
            'loginBackgroundImage' => '',
            'loginFootnote' => '',
            'loginHighlightColor' => '',
            'loginLogo' => '',
            'loginLogoAlt' => '',
        ],
        'extensionmanager' => [
            'automaticInstallation' => '1',
            'offlineMode' => '0',
        ],
        'nnhelpers' => [
            'clearAllCaches' => '0',
            'deeplApiKey' => '',
            'deeplApiUrl' => 'https://api-free.deepl.com/v2/translate',
            'devModeEnabled' => '0',
            'googleGeoApiKey' => '',
            'saltingKey' => 'WyJKY2dJc0UzZ1JDV2ZLZDVZMFBydTBsaWVQUFwvVXpQWXdoYjZZVzBzVmdZOD0iLCJVcVwvWkJhWVJrVEE5STFcL1l3KzBBaWtvQ3dXdHJGVUhabUtHMzV5K3Yxb0pPbHVLYmh1OWNOSFVYXC91dDFneTZHT29Sa0NzTERuRkFaTnJQSndjK1Y5Zz09Il0=',
            'showMod' => '1',
        ],
        'nnrestapi' => [
            'apiKeys' => 'examplefeUserName:exampleApiKey',
            'disableDefaultEndpoints' => '0',
            'disablePreCheck' => '0',
            'maxSessionLifetime' => '3600',
        ],
        'oauth2_client' => [
            'providers' => [
                'keycloak' => [
                    'description' => 'Login with ID.QA.SONIA.DE',
                    'label' => 'ID.QA.SONIA.DE',
                    'options' => [
                        'clientId' => 'typo3.dev',
                        'clientSecret' => '2dKDfcEvYtjqYFJmpn0Rv6T1C1VYNXQt',
                        'responseResourceOwnerId' => 'sub',
                        'scopeSeparator' => ' ',
                        'scopes' => [
                            'openid',
                            'profile',
                            'email',
                        ],
                        'urlAccessToken' => 'https://id.qa.sonia.de/realms/dev/protocol/openid-connect/token',
                        'urlAuthorize' => 'https://id.qa.sonia.de/realms/dev/protocol/openid-connect/auth',
                        'urlResourceOwnerDetails' => 'https://id.qa.sonia.de/realms/dev/protocol/openid-connect/userinfo',
                    ],
                    'scopes' => [
                        'frontend',
                        'backend',
                    ],
                ],
            ],
        ],
    ],
    'FE' => [
        'cacheHash' => [
            'enforceValidation' => true,
        ],
        'debug' => true,
        'disableNoCacheParameter' => true,
        'loginRateLimit' => 0,
        'passwordHashing' => [
            'className' => 'TYPO3\\CMS\\Core\\Crypto\\PasswordHashing\\Argon2iPasswordHash',
            'options' => [],
        ],
    ],
    'GFX' => [
        'processor' => 'GraphicsMagick',
        'processor_allowTemporaryMasksAsPng' => false,
        'processor_colorspace' => 'RGB',
        'processor_effects' => false,
        'processor_enabled' => true,
        'processor_path' => '/usr/bin/',
        'processor_path_lzw' => '/usr/bin/',
    ],
    'LOG' => [
        'TYPO3' => [
            'CMS' => [
                'deprecations' => [
                    'writerConfiguration' => [
                        'notice' => [
                            'TYPO3\CMS\Core\Log\Writer\FileWriter' => [
                                'disabled' => true,
                            ],
                        ],
                    ],
                ],
            ],
        ],
    ],
    'MAIL' => [
        'transport' => 'sendmail',
        'transport_sendmail_command' => '/usr/sbin/sendmail -t -i',
        'transport_smtp_encrypt' => '',
        'transport_smtp_password' => '',
        'transport_smtp_server' => '',
        'transport_smtp_username' => '',
    ],
    'SYS' => [
        'caching' => [
            'cacheConfigurations' => [
                'hash' => [
                    'backend' => 'TYPO3\\CMS\\Core\\Cache\\Backend\\Typo3DatabaseBackend',
                ],
                'imagesizes' => [
                    'backend' => 'TYPO3\\CMS\\Core\\Cache\\Backend\\Typo3DatabaseBackend',
                    'options' => [
                        'compression' => true,
                    ],
                ],
                'pages' => [
                    'backend' => 'TYPO3\\CMS\\Core\\Cache\\Backend\\Typo3DatabaseBackend',
                    'options' => [
                        'compression' => true,
                    ],
                ],
                'pagesection' => [
                    'backend' => 'TYPO3\\CMS\\Core\\Cache\\Backend\\Typo3DatabaseBackend',
                    'options' => [
                        'compression' => true,
                    ],
                ],
                'rootline' => [
                    'backend' => 'TYPO3\\CMS\\Core\\Cache\\Backend\\Typo3DatabaseBackend',
                    'options' => [
                        'compression' => true,
                    ],
                ],
            ],
        ],
        'devIPmask' => '',
        'displayErrors' => 0,
        'encryptionKey' => 'ecb5c46de32451b73a5c5b528d008aaf676d9f104e6cce6b3176dbb65d9ea7ed9fb6a9791c2daecffb52183bc42b324c',
        'exceptionalErrors' => 4096,
        'features' => [
            'yamlImportsFollowDeclarationOrder' => true,
        ],
        'sitename' => 'New TYPO3 site',
        'systemMaintainers' => [
            1,
        ],
    ],
];
