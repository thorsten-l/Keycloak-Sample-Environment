import 'package:app12/constants.dart';
import 'package:app12/services/authentication.dart';
import 'package:go_router/go_router.dart';
import 'package:flutter/cupertino.dart';

class CLoginScreen extends StatelessWidget {
  const CLoginScreen({super.key});

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
            Text(
              loginHost,
              style: TextStyle(fontSize: 28.0),
            ),
            const Text(
              loginText,
            ),
            SizedBox(height: 100),
            CupertinoButton.filled(
              child: const Text('Login'),
              onPressed: () {
                Authentication.instance
                    .authenticate(context)
                    .then((success) => {
                          if (success) {context.go("/details")}
                        });
              },
            ),
          ],
        ),
      ),
    );
  }
}
