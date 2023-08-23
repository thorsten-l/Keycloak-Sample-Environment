#!/bin/bash

if [ ! -f "/usr/local/apache2/conf/httpd.conf" ]; then
  echo "Initializing Apache2 directory."
  mkdir -p /usr/local/apache2
  cp -r /usr/local/apache2/dist/* /usr/local/apache2
fi
rm -f /usr/local/apache2/logs/httpd.pid
httpd -D FOREGROUND
