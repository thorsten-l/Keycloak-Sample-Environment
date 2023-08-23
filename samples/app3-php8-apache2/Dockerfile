FROM php:8.2.4-apache-bullseye

RUN rm /etc/apt/preferences.d/no-debian-php
RUN apt update -y && apt upgrade -y \
    && apt install -y curl php-json php-curl composer \
    && mkdir -p /var/www/.composer/cache/files \
    && chown -R www-data:www-data /var/www

USER www-data
WORKDIR /var/www/html

RUN composer require jumbojett/openid-connect-php \
  && composer dumpautoload -o

VOLUME [ "/var/www/html" ]

EXPOSE 80 443
