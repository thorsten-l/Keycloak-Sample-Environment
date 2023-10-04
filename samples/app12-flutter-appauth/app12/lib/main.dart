import 'dart:developer';

import 'package:app12/router.dart';
import 'package:flutter/material.dart';
import 'package:package_info_plus/package_info_plus.dart';

import 'constants.dart';

late PackageInfo appPackageInfo;

void main() async {

  WidgetsFlutterBinding.ensureInitialized();

  appPackageInfo = await PackageInfo.fromPlatform();

  log( appPackageInfo.appName, name: "appName" );
  log( appPackageInfo.packageName, name: "packageName" );
  log( appPackageInfo.version, name: "version" );
  log( appPackageInfo.buildNumber, name: "buildNumber" );

  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp.router(
      title: appTitle,
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.blue),
        useMaterial3: true,
      ),
      routeInformationProvider: router.routeInformationProvider,
      routeInformationParser: router.routeInformationParser,
      routerDelegate: router.routerDelegate,
      debugShowCheckedModeBanner: false,
    );
  }
}
