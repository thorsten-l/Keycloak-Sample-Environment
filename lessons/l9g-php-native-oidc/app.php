<?php
require_once 'config.php';
require_once 'OidcService.php';

use App\OidcService;

session_start();
if (!isset($_SESSION['tokens'])) {
    header('Location: index.php');
    exit();
}

$tokens = $_SESSION['tokens'];
$oidc = new OidcService($config);
$idTokenData = $oidc->decodeJwt($tokens['id_token']);
$accessTokenData = $oidc->decodeJwt($tokens['access_token']);
$refreshTokenData = $oidc->decodeJwt($tokens['refresh_token']);

// ID-Token und Redirect-URI definieren
$idToken = $tokens['id_token'];
$postLogoutRedirectUri = 'http://app1.dev.sonia.de:8081/';

// Logout-URL generieren
$logoutUrl = $oidc->getLogoutUrl($idToken, $postLogoutRedirectUri);

?>
<!DOCTYPE html>
<html>
<head>
    <title>App</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
    <div class="container mt-4">
        <h1>App</h1>
        <a class="btn btn-primary mt-4" href="<?php echo htmlspecialchars($logoutUrl); ?>">LOGOUT</a>
        <h2 class="mt-4">ID Token</h2>
        <pre><?php print_r($idTokenData); ?></pre>
        <h2 class="mt-4">Access Token</h2>
        <pre><?php print_r($accessTokenData); ?></pre>
        <h2 class="mt-4">Refresh Token</h2>
        <pre><?php print_r($refreshTokenData); ?></pre>
    </div>
</body>
</html>
