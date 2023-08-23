#!/bin/bash

docker compose down

sudo rm -f kcldap/data/run/slapd-localhost.pid kcldap/data/run/slapd-localhost.socket
sudo rm -fr kcdb/pg_stat_tmp
sudo xattr -rc .

docker compose up -d
docker compose logs -f
