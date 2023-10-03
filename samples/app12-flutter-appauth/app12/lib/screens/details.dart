import 'package:app12/services/authentication.dart';
import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';

class DetailsScreen extends StatefulWidget {
  const DetailsScreen({super.key});

  @override
  State<DetailsScreen> createState() => _DetailsScreen();
}

class _DetailsScreen extends State<DetailsScreen> {
  int currentPageIndex = 0;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        backgroundColor: Colors.lightBlue,
        foregroundColor: Colors.white,
        title: const Text("Information"),
        actions: [
          IconButton(
            onPressed: () {
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
            },
            icon: const Icon(Icons.logout),
          )
        ],
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
