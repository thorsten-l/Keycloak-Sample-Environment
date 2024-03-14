#!/bin/bash
docker run --rm --name sb31-oauth2-client -p 8090:8090 \
      -v `pwd`/data:/workspace/data sb31-oauth2-client:0.0.1-SNAPSHOT
