import 'package:app12/screens/details.dart';
import 'package:app12/screens/login.dart';
import 'package:app12/screens/splash.dart';
import 'package:flutter/material.dart';

const appTitle = 'App12 - Flutter OIDC AppAuth';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: appTitle,
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.blue),
        useMaterial3: true,
      ),
      initialRoute: "/",
      routes: {
        "/": (context) => const SplashScreen(),
        "/login": (context) => const LoginScreen(),
        "/details": (context) => const DetailsScreen(),
      },
    );
  }
}
