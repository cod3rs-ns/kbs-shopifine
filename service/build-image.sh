#!/usr/bin/env bash

# Create Docker Image
sbt clean dist
unzip target/universal/shopifine-core-service-*.zip -d docker
mv docker/shopifine-core-service-* docker/shopifine-core-service
docker build --rm -t shopifine-core docker

# Get Application Version
VERSION=$(grep 'version := ' build.sbt | sed 's:^[^"]*"\([^"]*\)".*:\1:')

# Tag and Push to Docker Registry
docker login --username ${DOCKER_USERNAME} --password ${DOCKER_PASSWORD}

docker tag shopifine-core dmarjanovic/shopifine-core:${VERSION}
docker push dmarjanovic/shopifine-core:${VERSION}
docker tag shopifine-core dmarjanovic/shopifine-core:latest
docker push dmarjanovic/shopifine-core:latest

# Remove Created Directory
rm -rf docker/shopifine-core-service
