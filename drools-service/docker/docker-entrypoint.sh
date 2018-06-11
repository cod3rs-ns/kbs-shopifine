#!/usr/bin/env bash
set -e

# start the container
if [ "$1" = 'drools-service' -a "$(id -u)" = '0' ]; then
    chown -R shopifine:shopifine .
    exec su-exec shopifine "$0" "$@"
fi

exec "$@"
