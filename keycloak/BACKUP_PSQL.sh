#!/bin/bash -v
docker exec kcdb pg_dumpall -U keycloak > kcdb_backup.sql
