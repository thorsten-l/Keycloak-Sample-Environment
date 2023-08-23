<?php

require __DIR__ . '/vendor/autoload.php';

use Jumbojett\OpenIDConnectClient;

if ( session_status() != PHP_SESSION_ACTIVE )
{
  session_start();
}

$authenticated = false;

if ( isset($_SESSION['oidcClient']) )
{
  $oidc = $_SESSION['oidcClient'];
  $data = $oidc->introspectToken($oidc->getAccessToken());
  
  if ($data->active)
  { 
    $authenticated = true;
  }
}

if( ! $authenticated )
{
  header("Location: /");
  exit();
}

?>
