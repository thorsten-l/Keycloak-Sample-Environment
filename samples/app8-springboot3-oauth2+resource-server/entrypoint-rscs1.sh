#!/bin/sh
cd /workspace
cp sb3-oauth2-resource-server/target/sb3-oauth2-resource-server.jar .
java -jar sb3-oauth2-resource-server.jar
