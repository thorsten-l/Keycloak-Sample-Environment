#!/bin/bash -v 

docker compose down
rm -fr typo3 mysql postgresql
docker compose -f docker-compose-step1.yaml up --build -d
sleep 10
docker cp typo3app:/var/www/html typo3
docker compose down
