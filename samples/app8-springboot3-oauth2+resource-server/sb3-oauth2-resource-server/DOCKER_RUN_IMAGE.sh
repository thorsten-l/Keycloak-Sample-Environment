#!/bin/bash
docker run --rm --name sb3-oauth2-client -p 8081:8081 \
      -v `pwd`/data:/workspace/data sb3-oauth2-client:0.0.1-SNAPSHOT
