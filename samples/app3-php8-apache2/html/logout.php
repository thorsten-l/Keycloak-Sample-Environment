<?php

require __DIR__ . '/oidcCheck.php';

session_destroy();

if( $authenticated )
{
  $oidc->signOut($oidc->getIdToken(), "http://app3.dev.sonia.de:8083");
}

?>

