<?php

require __DIR__ . '/vendor/autoload.php';

use Jumbojett\OpenIDConnectClient;

$oidc = new OpenIDConnectClient(
    'https://id.dev.sonia.de/realms/dev',
    'app3',
    'vdYuqnNOzcUcxiccMugTX7BAscqyiYZS'
);

# Demo is dealing with HTTP rather than HTTPS
$oidc->setHttpUpgradeInsecureRequests(false);

# default scope is "openid"
$oidc->addScope( "email" );
$oidc->addScope( "phone" );

$oidc->authenticate();

$_SESSION['oidcClient'] = $oidc;

header("Location: /userinfo.php");
exit();

?>

