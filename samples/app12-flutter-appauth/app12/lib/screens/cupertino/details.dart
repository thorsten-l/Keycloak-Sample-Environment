import 'dart:developer';

import 'package:app12/main.dart';
import 'package:app12/constants.dart';
import 'package:app12/screens/details.dart';
import 'package:app12/services/authentication.dart';
import 'package:flutter/material.dart';
import 'package:flutter/cupertino.dart';
import 'package:go_router/go_router.dart';

class CDetailsScreen extends State<DetailsScreen> implements DataHelper {
  int currentPageIndex = 0;
  SelectedItem? selectedMenu;

  @override
  Widget build(BuildContext context) {

    double height = (MediaQuery.of(context).size.height);

    log(height.toString(), name: "context.height");

    // iphone se 3rd - 667 -> 8
    // iphone 12 - 844 -> 32
    // iphone 15 pro max - 932 -> 44

    CupertinoNavigationBar cupertinoNavigationBar = CupertinoNavigationBar(
      trailing: GestureDetector(
        onTap: () {
          _showCupertinoMenu(context);
        },
        child: Icon(CupertinoIcons.ellipsis_circle),
      ),
      middle: const Text("Information"),
    );

    // TODO: There must be a better way to do this...
    double topInset = 8;
    if (height > 700) {
      topInset = 16;
    }
    if (height > 800) {
      topInset = 32;
    }
    if (height > 900) {
      topInset = 48;
    }

    log(topInset.toString(), name: "topInset");

    return CupertinoPageScaffold(
      navigationBar: cupertinoNavigationBar,
      child: SafeArea(
        top: false,
        bottom: true,
        minimum: EdgeInsets.only(top: topInset),
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
                    view = tokenView(Authentication.instance.idTokenMap()!);
                    break;

                  case 1: // access token
                    view =
                        tokenView(Authentication.instance.accessTokenMap()!);
                    break;

                  case 2: // userinfo
                    view = tokenView(Authentication.instance.userInfoMap()!);
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
                launchInBrowser(githubUrl);
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

  Widget createDataKey( String value )
  {
    return Text(
      value,
      style: TextStyle(
        fontWeight: FontWeight.bold,
      ),
    );
  }

  Widget createDataValue( String value )
  {
    return Text( value );
  }
}
