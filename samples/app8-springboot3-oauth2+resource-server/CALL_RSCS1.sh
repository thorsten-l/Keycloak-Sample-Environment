#!/bin/bash
echo ""
echo ""
curl -s -H "Authorization: Bearer $1" http://rscs1.dev.sonia.de:18088/buildinfo | jq
echo ""
echo ""
curl -s -H "Authorization: Bearer $1" http://rscs1.dev.sonia.de:18088/
