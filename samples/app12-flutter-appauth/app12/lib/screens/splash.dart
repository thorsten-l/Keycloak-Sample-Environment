import 'dart:developer';

import 'package:app12/services/authentication.dart';
import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:uuid/uuid.dart';

import '../constants.dart';

class SplashScreen extends StatefulWidget {
  const SplashScreen({super.key});

  @override
  State<SplashScreen> createState() => _SplashScreen();
}

class _SplashScreen extends State<SplashScreen> {

  late Widget _screen;
  bool _initialized = false;

  @override
  Widget build(BuildContext context) {

    if ( !_initialized ) {
      // update access token -> if refresh token exists
      Authentication.instance.updateAccessToken(context).then(
              (authenticated) =>
              context.go(authenticated ? "/details" : "/login"));

      _screen = Scaffold(
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
                style: Theme
                    .of(context)
                    .textTheme
                    .headlineMedium,
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

      _initialized = true;
    }

    return _screen;
  }
}
