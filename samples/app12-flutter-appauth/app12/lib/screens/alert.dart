import 'package:flutter/material.dart';
import 'package:flutter/cupertino.dart';
import 'package:universal_platform/universal_platform.dart';

void showErrorAlert(BuildContext context, String message) {
  if (UniversalPlatform.isIOS) {
    showCupertinoModalPopup<void>(
      context: context,
      builder: (BuildContext context) => CupertinoAlertDialog(
        title: Text(message),
      ),
    );
  } else {
    var snackBar = SnackBar(content: Text(message));
    ScaffoldMessenger.of(context).showSnackBar(snackBar);
  }
}
