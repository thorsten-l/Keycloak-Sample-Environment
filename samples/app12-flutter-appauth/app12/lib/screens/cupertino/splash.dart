import 'package:app12/constants.dart';
import 'package:flutter/cupertino.dart';

class CSplashScreen extends StatelessWidget {
  const CSplashScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return CupertinoPageScaffold(
      navigationBar: CupertinoNavigationBar(
        middle: const Text(appTitle),
      ),
      child: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            Padding(
              padding: const EdgeInsets.all(128),
              child: Image.asset('assets/images/logo.png'),
            ),
            Text(
              'Initializing',
              style: TextStyle(fontSize: 28.0),
            ),
            const Text(
              'Please wait...',
            ),
            const Padding(
              padding: EdgeInsets.all(32),
              child: CupertinoActivityIndicator(radius: 20.0),
            ),
          ],
        ),
      ),
    );
  }
}
