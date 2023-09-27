import 'package:app12/main.dart';
import 'package:flutter/material.dart';

class SplashPage extends StatelessWidget
{
  const SplashPage({super.key});

  @override
  Widget build(BuildContext context) {

    // refresh access token if refresh token exists

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
            Text(
              'Initializing',
              style: Theme.of(context).textTheme.headlineMedium,
            ),
            const Text(
              'Please wait...',
            ),
          ],
        ),
      ),
    );
  }
}