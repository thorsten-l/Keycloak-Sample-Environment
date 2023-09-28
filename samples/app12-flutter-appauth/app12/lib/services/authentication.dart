import 'dart:convert';
import 'dart:developer';
import 'package:flutter_appauth/flutter_appauth.dart';
import 'package:uuid/uuid.dart';

class Authentication {
  static final Authentication instance = Authentication._internal();

  static const String _oidcDiscoveryUrl =
      "https://id.dev.sonia.de/realms/dev/.well-known/openid-configuration";
  static const String _oidcClientId = "app12";
  static const String _oidcClientSecret = "0Jv69f5uaX14Xuel0ScDvGVVwXgTL6LM";
  static const String _oidcRedirectUrl = "com.example.app12://callback";

  final String _uuid = const Uuid().v4().toString();
  final FlutterAppAuth appAuth = const FlutterAppAuth();

  Authentication._internal() {
    log("uuid = $_uuid", name: "Authentication._internal");
  }

  bool updateAccessToken() {
    log("uid = $_uuid", name: "Authentication.updateAccessToken");
    return false;
  }

  Future<bool> authenticate() async {
    log("uid = $_uuid", name: "Authentication.authenticate");

    if (!_authenticated) {
      _authorizationTokenResponse = await appAuth.authorizeAndExchangeCode(
        AuthorizationTokenRequest(
          _oidcClientId,
          _oidcRedirectUrl,
          clientSecret: _oidcClientSecret,
          discoveryUrl: _oidcDiscoveryUrl,
          // scopes: ['openid', 'profile', 'email', 'offline_access'],
          scopes: ['openid', 'profile', 'email'],
          promptValues: ['login'],
        ),
      );

      if (_authorizationTokenResponse != null) {
        String? idToken = _authorizationTokenResponse!.idToken;
        log("idToken=${idToken!}");
        _authenticated = _validateIdToken(idToken);
      }
    }

    return _authenticated;
  }

  void logout() {
    log("uid = $_uuid", name: "Authentication.logout");
    _authenticated = false;
  }

  String _decodeTokenPart( String tokenPart )
  {
    return utf8.decode(base64Url.decode((base64Url.normalize(tokenPart))));
  }

  bool _validateIdToken(String? idToken) {
    bool idTokenValid = false;

    if (idToken != null) {
      var idTokenParts = idToken.split(r'.');
      if ( idTokenParts.length == 3) {
        log( "found all 3 parts in idToken");

        log( _decodeTokenPart(idTokenParts[0]), name: 'idToken Header' );
        log( _decodeTokenPart(idTokenParts[1]), name: 'idToken Body' );
        log( base64Url.normalize(idTokenParts[2]), name: 'idToken Signature' );

        idTokenMap = jsonDecode(_decodeTokenPart(idTokenParts[1]));
        log( idTokenMap!.toString(), name: "idTokenMap" );

        log( idTokenMap!['name'].toString(), name: "name");
        log( idTokenMap!['preferred_username'].toString(), name: "preferred_username");
        log( idTokenMap!['sub'].toString(), name: "sub");
        log( idTokenMap!['nickname'].toString(), name: "nickname");
        log( idTokenMap!['family_name'].toString(), name: "family_name");
        log( idTokenMap!['email'].toString(), name: "email");

        idTokenValid = true;
      }
    }

    return idTokenValid;
  }

  AuthorizationTokenResponse? _authorizationTokenResponse;
  Map<String, dynamic>? idTokenMap;

  bool get authenticated => _authenticated;

  bool _authenticated = false;
}
