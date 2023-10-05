import 'dart:developer';

import 'package:app12/main.dart';
import 'package:app12/services/authentication.dart';
import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:url_launcher/url_launcher.dart';

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

  Future<void> _launchInBrowser(String url) async {
    if (!await launchUrl(
      Uri.parse(url),
      mode: LaunchMode.externalApplication,
    )) {
      throw Exception('Could not launch $url');
    }
  }

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
          itemBuilder: (BuildContext context) => <PopupMenuEntry<SelectedItem>>[
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
        SingleChildScrollView(
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
            rows: Authentication.instance
                .idTokenMap()!
                .entries
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
        ),
        SingleChildScrollView(
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
            rows: Authentication.instance
                .accessTokenMap()!
                .entries
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
        ),
        SingleChildScrollView(
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
            rows: Authentication.instance
                .userInfoMap()!
                .entries
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
        ),
      ][currentPageIndex],
    );
  }
}
