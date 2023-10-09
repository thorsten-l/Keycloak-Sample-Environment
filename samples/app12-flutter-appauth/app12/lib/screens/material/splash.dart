import 'package:app12/constants.dart';
import 'package:flutter/material.dart';

class MSplashScreen extends StatelessWidget {
  const MSplashScreen({super.key});

  @override
  Widget build(BuildContext context) {
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
