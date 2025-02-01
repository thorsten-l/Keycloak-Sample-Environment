#!/bin/bash -v
rm -fr kcdb
docker compose -f pg16.yaml up -d
sleep 5
docker exec -i kcdb psql -U postgres < ./kcdb_backup.sql
docker exec -i kcdb psql -U postgres -c "ALTER USER keycloak WITH PASSWORD 'keycloak';"
docker compose -f pg16.yaml down
