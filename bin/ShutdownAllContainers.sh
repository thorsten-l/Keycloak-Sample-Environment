#!/bin/bash

for i in `ls samples`; do
  echo $i
  ( cd "samples/$i"; docker compose down )
done

for i in `ls appliances`; do
  echo $i
  ( cd "appliances/$i"; docker compose down )
done

( cd keycloak ; docker compose down )
