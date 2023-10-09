import 'dart:developer';
import 'package:app12/main.dart';
import 'package:app12/constants.dart';
import 'package:app12/screens/details.dart';
import 'package:app12/services/authentication.dart';
import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';

class MDetailsScreen extends State<DetailsScreen> {
  int currentPageIndex = 0;
  SelectedItem? selectedMenu;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        backgroundColor: Colors.lightBlue,
        foregroundColor: Colors.white,
        title: const Text("Information"),
        leading: PopupMenuButton<SelectedItem>(
          icon: const Icon(Icons.menu),
          initialValue: selectedMenu,
          onSelected: (SelectedItem item) {
            setState(
                  () {
                selectedMenu = item;

                switch (selectedMenu!) {
                  case SelectedItem.about:
                    showAboutDialog(
                        context: context,
                        applicationVersion: appPackageInfo.buildNumber +
                            " (" +
                            appPackageInfo.version +
                            ")",
                        applicationName: appPackageInfo.appName,
                        applicationLegalese:
                        "Copyleft 2023 by\nDr. Thorsten Ludewig\nt.ludewig@gmail.com");
                    break;

                  case SelectedItem.github:
                    log("launch browser");
                    launchInBrowser(githubUrl);
                    break;

                  case SelectedItem.logout:
                    showDialog(
                      context: context,
                      builder: (BuildContext context) {
                        return AlertDialog(
                          title: const Text("Logout"),
                          content: const Text("Do you really want to logout?"),
                          actions: [
                            TextButton(
                              child: const Text("No"),
                              onPressed: () {
                                Navigator.of(context).pop();
                              },
                            ),
                            TextButton(
                              child: const Text("Yes"),
                              onPressed: () {
                                Authentication.instance.logout();
                                context.go("/");
                              },
                            ),
                          ],
                        );
                      },
                    );
                    break;
                }
              },
            );
          },
          itemBuilder: (BuildContext context) =>
          <PopupMenuEntry<SelectedItem>>[
            const PopupMenuItem<SelectedItem>(
              value: SelectedItem.about,
              child: Text('About'),
            ),
            const PopupMenuItem<SelectedItem>(
              value: SelectedItem.github,
              child: Text('Source Code...'),
            ),
            const PopupMenuDivider(),
            const PopupMenuItem<SelectedItem>(
              value: SelectedItem.logout,
              child: Text('Logout'),
            ),
          ],
        ),
      ),
      bottomNavigationBar: NavigationBar(
        onDestinationSelected: (int index) {
          setState(() {
            currentPageIndex = index;
          });
        },
        //indicatorColor: Colors.amber[800],
        selectedIndex: currentPageIndex,
        destinations: const <Widget>[
          NavigationDestination(
            selectedIcon: Icon(Icons.person),
            icon: Icon(Icons.person_outlined),
            label: 'ID Token',
          ),
          NavigationDestination(
            selectedIcon: Icon(Icons.lock),
            icon: Icon(Icons.lock_outline),
            label: 'Access Token',
          ),
          NavigationDestination(
            selectedIcon: Icon(Icons.info),
            icon: Icon(Icons.info_outline),
            label: 'Userinfo',
          ),
        ],
      ),
      body: <Widget>[
        tokenView(Authentication.instance.idTokenMap()!),
        tokenView(Authentication.instance.accessTokenMap()!),
        tokenView(Authentication.instance.userInfoMap()!),
      ][currentPageIndex],
    );
  }

}