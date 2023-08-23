<?php
require __DIR__ . '/oidcCheck.php';

if( $authenticated )
{
  $issuer = $oidc->getIssuer();
  $uid = $oidc->requestUserInfo('preferred_username');
  $name = $oidc->requestUserInfo('name');
  $family_name = $oidc->requestUserInfo('family_name');
  # Achtung das subject kann sich ändern!
  $subject = $oidc->requestUserInfo('sub'); 

  # scope email
  $email = $oidc->requestUserInfo('email');
  # scope phone
  $phone_number = $oidc->requestUserInfo('phone_number');
}
?>

<html>
<head>
    <title>Example OpenID Connect Client Use</title>
    <style>
        body {
            font-family: 'Lucida Grande', Verdana, Arial, sans-serif;
        }
    </style>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-EVSTQN3/azprG1Anm3QDgpJLIm9Nao0Yz1ztcQTwFspd3yD65VohhpuuCOmLASjC" crossorigin="anonymous">
</head>
<body>
    <div style="margin-top: 2em" class="container">
    <h1>Protected</h1>
    <table class="table table-striped">
        <tbody>
        <tr><th>issuer</th><td><?php echo $issuer; ?></td></tr>
        <tr><th>uid</th><td><?php echo $uid; ?></td></tr>
        <tr><th>name (full name)</th><td><?php echo $name; ?></td></tr>
        <tr><th>family_name</th><td><?php echo $family_name; ?></td></tr>
        <tr><th>subject</th><td><?php echo $subject; ?> <b>do not use!</b></td></tr>
        <tr><th>email</th><td><?php echo $email; ?></td></tr>
        <tr><th>phone_number</th><td><?php echo $phone_number; ?></td></tr>
        </tbody>
    </table>

    <p style="margin-top: 3em">
    <a href="/logout.php" class="btn btn-warning btn-lg" role="button">LOGOUT</a>
    </p>
    </div>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js" integrity="sha384-MrcW6ZMFYlzcLA8Nl+NtUVF0sA7MsXsP1UyJoMp4YLEuNSfAP+JcXn/tWtIaxVXM" crossorigin="anonymous"></script>
</body>
</html>
