#!/bin/bash

curl -X POST 'https://id.dev.sonia.de/realms/dev/protocol/openid-connect/token' \
 --header 'Content-Type: application/x-www-form-urlencoded' \
 --data-urlencode 'grant_type=password' \
 --data-urlencode 'client_id=app8' \
 --data-urlencode 'client_secret=018opzer6HsHeQaVPMGCQpSn8pNdFhmM' \
 --data-urlencode 'username=c1test1' \
 --data-urlencode 'password=test123' | jq access_token
