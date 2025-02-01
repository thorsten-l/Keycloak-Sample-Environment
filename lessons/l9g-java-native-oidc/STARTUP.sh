#!/bin/bash
export JAVA_HOME=`/usr/libexec/java_home -v 21`
mvn clean package
docker compose up --build --remove-orphans

