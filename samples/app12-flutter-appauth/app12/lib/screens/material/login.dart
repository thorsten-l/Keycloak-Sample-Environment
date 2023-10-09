import 'package:app12/constants.dart';
import 'package:app12/services/authentication.dart';
import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';

class MLoginScreen extends StatelessWidget {
  const MLoginScreen({super.key});

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
            loginHost,
            style: Theme.of(context).textTheme.headlineMedium,
          ),
          const Text(
            loginText,
          ),
        ],
      )),
      floatingActionButton: FloatingActionButton.extended(
        onPressed: () {
          Authentication.instance.authenticate(context).then(
                (success) => {
                  if (success) {context.go("/details")}
                },
              );
        },
        label: const Text('Login'),
        icon: const Icon(Icons.login),
      ),
    );
  }
}
