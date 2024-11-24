#!/bin/bash
docker compose up --build --remove-orphans -d
echo wait 5s
sleep 5
docker compose down
docker compose -f docker-compose-composer-install.yml up
docker compose down
docker compose up
