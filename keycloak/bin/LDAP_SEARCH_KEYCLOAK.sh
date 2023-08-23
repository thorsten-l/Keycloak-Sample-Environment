#!/bin/bash

ldapsearch -h id.dev.sonia.de -p 3389 \
    -D 'cn=keycloak,ou=application,dc=sample,dc=org' -w 'admin' \
    -b 'dc=sample,dc=org' -s sub \
    '(objectClass=inetOrgPerson)'
