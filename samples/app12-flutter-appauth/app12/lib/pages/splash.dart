import 'dart:async';

import 'package:app12/main.dart';
import 'package:flutter/material.dart';

class SplashPage extends StatelessWidget
{
  const SplashPage({super.key});

  @override
  Widget build(BuildContext context) {

    // refresh access token -> if refresh token exists

    // simulate refresh
    Timer(const Duration(seconds: 5), () {
      Navigator.pushReplacementNamed(context, "/login");
    });

    return Scaffold(
      appBar: AppBar(
        backgroundColor: Colors.lightBlue,
        foregroundColor: Colors.white,
        title: const Text(appTitle),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            Padding(
              padding: const EdgeInsets.all(128),
              child: Image.asset('assets/images/logo.png'),
            ),
            Text(
              'Initializing',
              style: Theme.of(context).textTheme.headlineMedium,
            ),
            const Text(
              'Please wait...',
            ),
            const Padding(
              padding: EdgeInsets.all(32),
              child: LinearProgressIndicator(),
            ),
          ],
        ),
      ),
    );
  }
}