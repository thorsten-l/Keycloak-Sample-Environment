import 'package:app12/services/authentication.dart';
import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';

import '../constants.dart';

class DetailsScreen extends StatelessWidget {
  const DetailsScreen({super.key});

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
            'Details',
            style: Theme.of(context).textTheme.headlineMedium,
          ),
          const Text(
            '...',
          ),
        ],
      )),
      floatingActionButton: FloatingActionButton.extended(
        onPressed: () {
          Authentication.instance.logout();
          context.go("/");
        },
        label: const Text('Logout'),
        icon: const Icon(Icons.logout),
      ),
    );
  }
}
