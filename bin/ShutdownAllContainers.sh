#!/bin/bash

for i in `ls samples`; do
  echo $i
  ( cd "samples/$i"; docker compose down )
done

( cd appliance ; docker compose down )
( cd keycloak ; docker compose down )
