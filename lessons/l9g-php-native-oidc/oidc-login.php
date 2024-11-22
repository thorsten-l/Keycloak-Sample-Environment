<?php
require_once 'config.php';
require_once 'OidcService.php';

use App\OidcService;

session_start();

if (isset($_GET['code']) && isset($_GET['state'])) {
    $code = $_GET['code'];
    $state = $_GET['state'];

    if ($state !== $_SESSION['oauth2_state']) {
        die('Invalid state');
    }

    $oidc = new OidcService($config);
    $tokens = $oidc->exchangeCodeForTokens($code);

    error_log(print_r($tokens, true));

    $_SESSION['tokens'] = $tokens;
    header('Location: app.php');
    exit();
} else {
    die('Missing code or state');
}
