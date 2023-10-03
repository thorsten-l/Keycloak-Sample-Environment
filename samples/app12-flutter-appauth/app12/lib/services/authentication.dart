import 'dart:convert';
import 'dart:developer';
import 'package:flutter_appauth/flutter_appauth.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:uuid/uuid.dart';

import '../constants.dart';

class Authentication {
  static final Authentication instance = Authentication._internal();

  final String _uuid = const Uuid().v4().toString();
  final FlutterAppAuth appAuth = const FlutterAppAuth();
  final FlutterSecureStorage secureStorage = const FlutterSecureStorage();

  Authentication._internal() {
    log("uuid = $_uuid", name: "Authentication._internal");
  }

  Future<bool> updateAccessToken() async {
    log("uuid = $_uuid", name: "Authentication.updateAccessToken");

    String? idToken = await secureStorage.read(key: idTokenKey);

    if (idToken != null) {
      _validateIdToken(idToken);
    }

    String? refreshToken = await secureStorage.read(key: refreshTokenKey);

    if (refreshToken != null) {
      // TODO: validate refresh token and get new access token
      _authenticated = true;
    }

    // Show splash screen at least for a second
    return Future.delayed(const Duration(seconds: 1), () => _authenticated);
  }

  Future<bool> authenticate() async {
    log("uuid = $_uuid", name: "Authentication.authenticate");

    if (!_authenticated) {
      _authorizationTokenResponse = await appAuth.authorizeAndExchangeCode(
        AuthorizationTokenRequest(
          oidcClientId,
          oidcRedirectUrl,
          clientSecret: oidcClientSecret,
          discoveryUrl: oidcDiscoveryUrl,
          // scopes: ['openid', 'profile', 'email', 'offline_access'],
          scopes: ['openid', 'profile', 'email'],
          promptValues: ['login'],
        ),
      );

      if (_authorizationTokenResponse != null) {
        String? idToken = _authorizationTokenResponse!.idToken;
        log("idToken=${idToken!}");
        _authenticated = _validateIdToken(idToken);
        if (_authenticated) {
          secureStorage.write(
              key: authorizedKey, value: _authenticated.toString());
          secureStorage.write(
              key: idTokenKey, value: _authorizationTokenResponse!.idToken);
          secureStorage.write(
              key: accessTokenKey,
              value: _authorizationTokenResponse!.accessToken);
          secureStorage.write(
              key: refreshTokenKey,
              value: _authorizationTokenResponse!.refreshToken);
        }
      }
    }

    return _authenticated;
  }

  void logout() async {
    log("uuid = $_uuid", name: "Authentication.logout");
    _authenticated = false;
    secureStorage.deleteAll();
  }

  String _decodeTokenPart(String tokenPart) {
    return utf8.decode(base64Url.decode((base64Url.normalize(tokenPart))));
  }

  bool _validateIdToken(String? idToken) {
    bool idTokenValid = false;

    if (idToken != null) {
      var idTokenParts = idToken.split(r'.');
      if (idTokenParts.length == 3) {
        log("found all 3 parts in idToken");

        log(_decodeTokenPart(idTokenParts[0]), name: 'idToken Header');
        log(_decodeTokenPart(idTokenParts[1]), name: 'idToken Body');
        log(base64Url.normalize(idTokenParts[2]), name: 'idToken Signature');

        idTokenMap = jsonDecode(_decodeTokenPart(idTokenParts[1]));
        log(idTokenMap!.toString(), name: "idTokenMap");

        log(idTokenMap!['name'].toString(), name: "name");
        log(idTokenMap!['preferred_username'].toString(),
            name: "preferred_username");
        log(idTokenMap!['sub'].toString(), name: "sub");
        log(idTokenMap!['nickname'].toString(), name: "nickname");
        log(idTokenMap!['family_name'].toString(), name: "family_name");
        log(idTokenMap!['email'].toString(), name: "email");

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
