#!/bin/bash

docker compose down

sudo rm -f kcldap/data/run/slapd-localhost.pid kcldap/data/run/slapd-localhost.socket
sudo rm -fr kcdb/pg_stat_tmp

if [ "Darwin" = `uname -s` ]; then
  echo "Hello macOS user, cleaning up extended attributes."
  sudo xattr -rc .
  sleep 2
fi

docker compose up -d
docker compose logs -f
