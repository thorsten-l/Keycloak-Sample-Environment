<?php
require_once 'config.php';
require_once 'OidcService.php';

use App\OidcService;

session_start();
$oidc = new OidcService($config);

$state = bin2hex(random_bytes(16));
$_SESSION['oauth2_state'] = $state;

$authUrl = $oidc->getAuthorizationUrl($state);
?>
<!DOCTYPE html>
<html>
<head>
    <title>Home</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
    <div class="container mt-4">
        <h1>Home</h1>
        <a class="btn btn-primary mt-4" href="<?php echo $authUrl; ?>">LOGIN</a>
    </div>
</body>
</html>