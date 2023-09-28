import 'package:app12/screens/details.dart';
import 'package:app12/screens/login.dart';
import 'package:app12/screens/splash.dart';
import 'package:go_router/go_router.dart';

final router = GoRouter(
  routes: [
    GoRoute(
      name: "login",
      path: "/login",
      builder: (context, state) => const LoginScreen(),
    ),
    GoRoute(
      name: "splash",
      path: "/",
      builder: (context, state) => const SplashScreen(),
    ),
    GoRoute(
      name: "details",
      path: "/details",
      builder: (context, state) => const DetailsScreen(),
    ),
  ],
  initialLocation: '/',
);
