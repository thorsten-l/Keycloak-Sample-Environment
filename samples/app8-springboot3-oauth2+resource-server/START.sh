#!/bin/bash

mvn clean package
cp sb3-oauth2-client/target/sb3-oauth2-client.jar .
cp sb3-oauth2-resource-server/target/sb3-oauth2-resource-server.jar .

docker compose up
