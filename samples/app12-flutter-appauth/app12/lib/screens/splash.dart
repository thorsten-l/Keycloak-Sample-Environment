import 'dart:async';
import 'package:app12/services/authentication.dart';
import 'package:app12/main.dart';
import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';

class SplashScreen extends StatelessWidget
{
  const SplashScreen({super.key});

  @override
  Widget build(BuildContext context) {

    // update access token -> if refresh token exists
    Authentication.instance.updateAccessToken();

    // simulate refresh
    Timer(const Duration(seconds: 2), ()
    {
      context.go( Authentication.instance.authenticated ? "/details" : "/login" );
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