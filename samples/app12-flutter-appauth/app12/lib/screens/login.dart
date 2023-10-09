import 'package:flutter/material.dart';
import 'package:universal_platform/universal_platform.dart';
import 'cupertino/login.dart';
import 'material/login.dart';

class LoginScreen extends StatelessWidget {
  const LoginScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return UniversalPlatform.isIOS ? CLoginScreen() : MLoginScreen();
  }
}
