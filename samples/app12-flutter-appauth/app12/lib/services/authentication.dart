import 'dart:convert';
import 'dart:developer';
import 'package:flutter/material.dart';
import 'package:flutter_appauth/flutter_appauth.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:uuid/uuid.dart';
import 'package:http/http.dart' as http;
import 'package:flutter/cupertino.dart';
import 'package:app12/constants.dart';
import 'package:app12/screens/alert.dart';

class Authentication {
  static final Authentication instance = Authentication._internal();

  final String _uuid = const Uuid().v4().toString();
  final FlutterAppAuth appAuth = const FlutterAppAuth();
  final FlutterSecureStorage secureStorage = const FlutterSecureStorage();

  Authentication._internal() {
    log("uuid = $_uuid", name: "Authentication._internal");
  }

  Future<bool> updateAccessToken(BuildContext context) async {
    log("uuid = $_uuid", name: "Authentication.updateAccessToken");

    _authenticated = false;
    _userInfo = null;

    _idToken = await secureStorage.read(key: idTokenKey);
    _accessToken = await secureStorage.read(key: accessTokenKey);
    _refreshToken = await secureStorage.read(key: refreshTokenKey);

    if (_refreshToken != null) {
      try {
        final response = await appAuth.token(
          TokenRequest(
            oidcClientId,
            oidcRedirectUrl,
            clientSecret: oidcClientSecret,
            discoveryUrl: oidcDiscoveryUrl,
            refreshToken: _refreshToken,
          ),
        );

        if (response != null) {
          _authenticated = true;
          _accessToken = response.accessToken;
          DateTime? exp = response.accessTokenExpirationDateTime;
          log("$exp", name: "access token expiration");
          await _getUserInfo(accessToken!);
        }
      } catch (error) {
        showErrorAlert(context, "Refresh token invalid!");
        log("refresh token invalid", name: "updateAccessToken");
        log(error.toString(), name: "updateAccessToken");
      }
    }

    // Show splash screen at least for two seconds
    return Future.delayed(const Duration(seconds: 2), () => _authenticated);
  }

  Future<bool> authenticate(BuildContext context) async {
    log("uuid = $_uuid", name: "Authentication.authenticate");

    if (!_authenticated) {
      try {
        AuthorizationTokenResponse? authorizationTokenResponse =
            await appAuth.authorizeAndExchangeCode(
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

        if (authorizationTokenResponse != null) {
          _idToken = authorizationTokenResponse.idToken;
          _accessToken = authorizationTokenResponse.accessToken;
          _refreshToken = authorizationTokenResponse.refreshToken;

          log(_idToken!, name: "idToken");

          _authenticated = _validateIdToken(_idToken);

          if (_authenticated) {
            secureStorage.write(key: idTokenKey, value: _idToken);
            secureStorage.write(key: accessTokenKey, value: _accessToken);
            secureStorage.write(key: refreshTokenKey, value: _refreshToken);
            log("_getUserInfo...");

            await _getUserInfo(accessToken!);
          }
        }
      } catch (error, stackTrace) {
        showErrorAlert(context, "Login failed!");
        log("login failed", name: "authenticate");
        log(error.toString(), name: "authenticate");
        log(stackTrace.toString(), name: "authenticate");
      }
    }

    return _authenticated;
  }

  Future<String?> _getUserInfo(String accessToken) async {
    // get userinfoEndpoint URL String from Discovery URL
    final discoveryResponse = await http.get(Uri.parse(oidcDiscoveryUrl));
    final userinfoEndpointUrl =
        jsonDecode(discoveryResponse.body)['userinfo_endpoint'];

    final userinfoResponse = await http.get(
      Uri.parse(userinfoEndpointUrl),
      headers: {
        'Authorization': 'Bearer $accessToken',
      },
    );

    if (userinfoResponse.statusCode == 200) {
      _userInfo = userinfoResponse.body;
    } else {
      _userInfo = null;
    }

    return _userInfo;
  }

  void logout() async {
    log("uuid = $_uuid", name: "Authentication.logout");
    _authenticated = false;
    secureStorage.deleteAll();
  }

  Map<String, dynamic>? parseJwt(String? jwt) {
    Map<String, dynamic>? tokenMap;

    if (jwt != null) {
      var jwtParts = jwt.split(r'.');
      if (jwtParts.length == 3) {
        tokenMap = jsonDecode(_decodeTokenPart(jwtParts[1]));
      }
    }

    return tokenMap;
  }

  String _decodeTokenPart(String tokenPart) {
    return utf8.decode(base64Url.decode((base64Url.normalize(tokenPart))));
  }

  bool _validateIdToken(String? idToken) {
    bool idTokenValid = false;

    Map<String, dynamic>? idTokenMap = parseJwt(idToken);

    if (idTokenMap != null) {
      log(idTokenMap.toString(), name: "idTokenMap");

      log(idTokenMap['name'], name: "name");
      log(idTokenMap['preferred_username'], name: "preferred_username");
      log(idTokenMap['sub'], name: "sub");
      log(idTokenMap['given_name'], name: "given_name");
      log(idTokenMap['family_name'], name: "family_name");
      log(idTokenMap['email'], name: "email");

      idTokenValid = true;
    }

    return idTokenValid;
  }

  Map<String, dynamic>? idTokenMap() {
    Map<String, dynamic>? idTokenMap = <String, dynamic>{};
    if( _idToken != null ) {
      idTokenMap = parseJwt(_idToken);
    }
    return idTokenMap;
  }

  Map<String, dynamic>? accessTokenMap() {
    Map<String, dynamic>? accessTokenMap = <String, dynamic>{};
    if( _accessToken != null ) {
      accessTokenMap = parseJwt(_accessToken);
    }
    return accessTokenMap;
  }

  Map<String, dynamic>? userInfoMap() {
    var userinfoMap = <String, dynamic>{};
    if (_userInfo != null) {
      userinfoMap = jsonDecode(_userInfo!);
    }
    return userinfoMap;
  }

  bool get authenticated => _authenticated;
  String? get accessToken => _accessToken;

  String? _idToken;
  String? _accessToken;
  String? _refreshToken;
  String? _userInfo;
  bool _authenticated = false;
}
