#!/bin/bash

( cd keycloak ; docker compose down )

for i in `ls samples`; do
  echo $i
  ( cd "samples/$i"; docker compose down )
done

find . \( -name 'target' \) -print -exec rm -fr {} \;
find . \( -name '.DS_Store' -o -name '._?*' -o -name 'access_log*' -o -name '*.socket' -o -name '*.sock' -o -name 'application.log'  \) -print -exec rm -fr {} \;
rm -f keycloak/kcldap/data/run/slapd-localhost.socket keycloak/kcfront/log/* keycloak/kcldap/data/logs/*

( cd ./samples/app12-flutter-appauth/app12; flutter clean )

sudo xattr -rc .

cd ..

TIMESTAMP=`date '+%Y%m%d%H%M'`
echo $TIMESTAMP
rm -f Keycloak-Sample-Environment.tar.gz Keycloak-Sample-Environment.zip
gtar -c --no-acls --no-xattrs -vz --exclude=.git --exclude=private -f Keycloak-Sample-Environment.tar.gz Keycloak-Sample-Environment
cp Keycloak-Sample-Environment.tar.gz Keycloak-Sample-Environment-$TIMESTAMP.tar.gz
zip -r -X -9 Keycloak-Sample-Environment.zip Keycloak-Sample-Environment -x 'Keycloak-Sample-Environment/.git*' -x 'Keycloak-Sample-Environment/private*'

