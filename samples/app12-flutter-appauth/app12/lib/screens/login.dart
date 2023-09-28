import 'package:app12/main.dart';
import 'package:app12/services/authentication.dart';
import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';

class LoginScreen extends StatelessWidget {
  const LoginScreen({super.key});

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
          Text(
            'Login via id.dev.sonia.de',
            style: Theme.of(context).textTheme.headlineMedium,
          ),
          const Text(
            'User: c1test1 / Password: test123',
          ),
        ],
      )),
      floatingActionButton: FloatingActionButton.extended(
        onPressed: () async {

          var a = await Authentication.instance.authenticate();

          if (a) {
            context.go("/details");
          }
        },
        label: const Text('Login'),
        icon: const Icon(Icons.login),
      ),
    );
  }
}
