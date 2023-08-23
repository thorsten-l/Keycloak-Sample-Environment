#!/bin/bash

docker compose down
rm -f kcldap/data/run/slapd-localhost.socket
sudo xattr -rc .
docker compose up -d
docker compose logs -f
