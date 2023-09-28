import 'package:flutter/cupertino.dart';
import 'dart:developer';

class Authentication {
  static final Authentication _instance = Authentication._internal();

  static const String _OIDC_ISSUER = "https://id.dev.sonia.de/realms/dev";
  static const String _OIDC_CLIENT_ID = "app12";
  static const String _OIDC_CLIENT_SECRET = "0Jv69f5uaX14Xuel0ScDvGVVwXgTL6LM";

  static final String _uuid = UniqueKey().toString();

  bool authenticated = false;

  factory Authentication() {
    return _instance;
  }

  Authentication._internal() {
    log("uuid = $_uuid", name: "Authentication._internal");
    // initialize
  }

  bool updateAccessToken()
  {
    log( "uid = $_uuid", name: "Authentication.updateAccessToken" );
    return false;
  }

  bool authenticate()
  {
    log( "uid = $_uuid", name: "Authentication.authenticate" );
    authenticated = true;
    return true;
  }

  void logout()
  {
    log( "uid = $_uuid", name: "Authentication.logout" );
    authenticated = false;
  }
}