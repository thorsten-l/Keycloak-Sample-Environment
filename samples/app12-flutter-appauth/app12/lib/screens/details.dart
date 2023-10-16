import 'package:app12/screens/cupertino/details.dart';
import 'package:app12/screens/material/details.dart';
import 'package:flutter/material.dart';
import 'package:url_launcher/url_launcher.dart';
import 'package:universal_platform/universal_platform.dart';

enum SelectedItem { about, github, logout }

late DataHelper dataHelper;

class DetailsScreen extends StatefulWidget {
  const DetailsScreen({super.key});

  @override
  State<DetailsScreen> createState() {
    State<DetailsScreen> detailScreen =
        UniversalPlatform.isIOS ? CDetailsScreen() : MDetailsScreen();
    dataHelper = detailScreen as DataHelper;
    return detailScreen;
  }
}

Future<void> launchInBrowser(String url) async {
  if (!await launchUrl(
    Uri.parse(url),
    mode: LaunchMode.externalApplication,
  )) {
    throw Exception('Could not launch $url');
  }
}

Widget keyView(Map<String, dynamic> tokenMap) {
  return DataTable(
    decoration: BoxDecoration(
      border: Border(
        right: BorderSide(
          color: Colors.grey,
          width: 0.5,
        ),
      ),
    ),
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
    ],
    rows: tokenMap.entries
        .map(
          (e) => DataRow(cells: [
            DataCell(
              dataHelper.createDataKey(e.key.toString()),
            ),
          ]),
        )
        .toList(),
  );
}

Widget valueView(Map<String, dynamic> tokenMap) {
  return Expanded(
    child: SingleChildScrollView(
      scrollDirection: Axis.horizontal,
      child: DataTable(
        columns: const <DataColumn>[
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
                  dataHelper.createDataValue(e.value.toString()),
                ),
              ]),
            )
            .toList(),
      ),
    ),
  );
}

Widget tokenView(Map<String, dynamic> tokenMap) {
  return SingleChildScrollView(
    child: Row(
      children: [
        keyView(tokenMap),
        valueView(tokenMap),
      ],
    ),
  );
}

abstract class DataHelper {
  Widget createDataKey(String value);
  Widget createDataValue(String value);
}
