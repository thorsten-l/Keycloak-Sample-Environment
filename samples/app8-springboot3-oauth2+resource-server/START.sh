#!/bin/bash

if [ "Darwin" = `uname -s` ]; then
  export JAVA_HOME=`/usr/libexec/java_home -v 17`
  export PATH=$JAVA_HOME/bin:$PATH
fi

mvn clean package
cp sb3-oauth2-client/target/sb3-oauth2-client.jar .
cp sb3-oauth2-resource-server/target/sb3-oauth2-resource-server.jar .

docker compose up
