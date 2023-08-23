#!/bin/bash

if ! [ -d ./app7 ]; then
    
    echo "Initilizing app7.dev.sonia.de"
    mkdir -p ./app7 && cd ./app7
    django-admin startproject app7 .
    django-admin startapp app_app7

else
    cd ./app7

fi

echo "Starting app7.dev.sonia.de"
python manage.py makemigrations
python manage.py migrate
python manage.py runserver 0.0.0.0:8000
