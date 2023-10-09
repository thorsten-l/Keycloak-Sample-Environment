import 'package:app12/screens/cupertino/splash.dart';
import 'package:app12/screens/material/splash.dart';
import 'package:app12/services/authentication.dart';
import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:flutter/cupertino.dart';
import 'package:universal_platform/universal_platform.dart';

class SplashScreen extends StatefulWidget {
  const SplashScreen({super.key});

  @override
  State<SplashScreen> createState() => _SplashScreen();
}

class _SplashScreen extends State<SplashScreen> {
  late Widget _screen;
  bool _initialized = false;

  @override
  void didChangeDependencies() {
    _initialized = false;
    super.didChangeDependencies();
  }

  @override
  void dispose() {
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    if (!_initialized) {
      Authentication.instance.updateAccessToken(context).then(
          (authenticated) => context.go(authenticated ? "/details" : "/login"));
      _screen = UniversalPlatform.isIOS ? CSplashScreen() : MSplashScreen();
      _initialized = true;
    }
    return _screen;
  }
}
