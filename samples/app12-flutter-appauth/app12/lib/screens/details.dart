import 'dart:developer';

import 'package:app12/main.dart';
import 'package:app12/services/authentication.dart';
import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:url_launcher/url_launcher.dart';
import 'package:flutter/cupertino.dart';
import 'package:universal_platform/universal_platform.dart';

import '../constants.dart';

class DetailsScreen extends StatefulWidget {
  const DetailsScreen({super.key});

  @override
  State<DetailsScreen> createState() => _DetailsScreen();
}

enum SelectedItem { about, github, logout }

class _DetailsScreen extends State<DetailsScreen> {
  int currentPageIndex = 0;
  SelectedItem? selectedMenu;

  @override
  Widget build(BuildContext context) {
    return (UniversalPlatform.isIOS)
        ? CupertinoPageScaffold(
            navigationBar: CupertinoNavigationBar(
              trailing: GestureDetector(
                onTap: () {
                  _showCupertinoMenu(context);
                },
                child: Icon(CupertinoIcons.ellipsis_circle),
              ),
              middle: const Text("Information"),
            ),
            child: CupertinoTabScaffold(
              tabBar: CupertinoTabBar(
                items: const <BottomNavigationBarItem>[
                  BottomNavigationBarItem(
                    icon: Icon(CupertinoIcons.person),
                    label: "ID Token",
                  ),
                  BottomNavigationBarItem(
                    icon: Icon(CupertinoIcons.lock),
                    label: "Access Token",
                  ),
                  BottomNavigationBarItem(
                    icon: Icon(CupertinoIcons.info),
                    label: "Userinfo",
                  ),
                ],
              ),
              backgroundColor: CupertinoColors.white,
              tabBuilder: (BuildContext context, int index) {
                return CupertinoTabView(
                  builder: (BuildContext context) {
                    Widget view = Center(
                      child: Icon(Icons.error),
                    );

                    switch (index) {
                      case 0: // id token
                        view =
                            _tokenView(Authentication.instance.idTokenMap()!);
                        break;

                      case 1: // access token
                        view = _tokenView(
                            Authentication.instance.accessTokenMap()!);
                        break;

                      case 2: // userinfo
                        view =
                            _tokenView(Authentication.instance.userInfoMap()!);
                        break;

                      default:
                    }

                    return Container(
                      margin: EdgeInsets.symmetric(vertical: 56),
                      child: view,
                    );
                  },
                );
              },
            ),
          )
        : Scaffold(
            appBar: AppBar(
              backgroundColor: Colors.lightBlue,
              foregroundColor: Colors.white,
              title: const Text("Information"),
              leading: PopupMenuButton<SelectedItem>(
                icon: const Icon(Icons.menu),
                initialValue: selectedMenu,
                // Callback that sets the selected popup menu item.
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
                          _launchInBrowser(githubUrl);
                          break;

                        case SelectedItem.logout:
                          showDialog(
                            context: context,
                            builder: (BuildContext context) {
                              return AlertDialog(
                                title: const Text("Logout"),
                                content:
                                    const Text("Do you really want to logout?"),
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
              _tokenView(Authentication.instance.idTokenMap()!),
              _tokenView(Authentication.instance.accessTokenMap()!),
              _tokenView(Authentication.instance.userInfoMap()!),
            ][currentPageIndex],
          );
  }

  Widget _tokenView(Map<String, dynamic> tokenMap) {
    return SingleChildScrollView(
      child: DataTable(
        columns: const <DataColumn>[
          DataColumn(
            label: Text(
              'Key',
              style: TextStyle(
                color: Colors.blue,
                fontWeight: FontWeight.bold,
              ),
            ),
          ),
          DataColumn(
            label: Text(
              'Value',
              style: TextStyle(
                color: Colors.blue,
                fontWeight: FontWeight.bold,
              ),
            ),
          ),
        ],
        rows: tokenMap.entries
            .map(
              (e) => DataRow(cells: [
                DataCell(
                  SizedBox(
                    width: 96,
                    child: Text(e.key.toString()),
                  ),
                ),
                DataCell(Text(e.value.toString())),
              ]),
            )
            .toList(),
      ),
    );
  }

  void _showCupertinoMenu(BuildContext context) {
    showCupertinoModalPopup(
      context: context,
      builder: (BuildContext context) {
        return CupertinoActionSheet(
          actions: <Widget>[
            CupertinoActionSheetAction(
              child: Text('About'),
              onPressed: () {
                Navigator.pop(context);
                _showCupertinoAboutDialog(context);
              },
            ),
            CupertinoActionSheetAction(
              child: Text('Source code ...'),
              onPressed: () {
                Navigator.pop(context);
                log("launch browser");
                _launchInBrowser(githubUrl);
              },
            ),
          ],
          cancelButton: CupertinoActionSheetAction(
            child: Text('Logout'),
            onPressed: () {
              Navigator.pop(context);
              _showLogoutDialog(context);
            },
          ),
        );
      },
    );
  }

  Future<void> _launchInBrowser(String url) async {
    if (!await launchUrl(
      Uri.parse(url),
      mode: LaunchMode.externalApplication,
    )) {
      throw Exception('Could not launch $url');
    }
  }

  void _showCupertinoAboutDialog(BuildContext context) {
    showCupertinoDialog(
      context: context,
      builder: (BuildContext context) {
        return CupertinoAlertDialog(
          title: Text('About ' + appPackageInfo.appName),
          content: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: <Widget>[
              Text('App Name: ' + appPackageInfo.appName),
              Text('Version: ' +
                  appPackageInfo.buildNumber +
                  " (" +
                  appPackageInfo.version +
                  ")"),
              Text('Author: Dr. Thorsten Ludewig'),
              Text('eMail: t.ludewig@gmail.com'),
              // Weitere Informationen hinzufügen, falls gewünscht
            ],
          ),
          actions: <Widget>[
            CupertinoDialogAction(
              child: Text('OK'),
              onPressed: () {
                Navigator.pop(context);
              },
            ),
          ],
        );
      },
    );
  }

  void _showLogoutDialog(BuildContext context) {
    showCupertinoDialog(
      context: context,
      builder: (BuildContext context) {
        return CupertinoAlertDialog(
          title: Text('Logout'),
          content: Text('Do you really want to proceed?'),
          actions: <Widget>[
            CupertinoDialogAction(
              child: Text('No'),
              onPressed: () {
                Navigator.pop(context, false);
              },
            ),
            CupertinoDialogAction(
              child: Text('Yes'),
              onPressed: () {
                Navigator.pop(context, true);
                log("cupertino logout - yes");
                Authentication.instance.logout();
                context.go("/");
              },
            ),
          ],
        );
      },
    );
  }
}
