#!/usr/bin/env bash

# Create Docker Image
sbt clean dist
unzip target/universal/drools-service-*.zip -d docker
mv docker/drools-service-* docker/drools-service
docker build --rm -t shopifine-drools docker

# Get Application Version
VERSION=$(grep 'version := ' build.sbt | sed 's:^[^"]*"\([^"]*\)".*:\1:')

# Tag and Push to Docker Registry
docker login --username ${DOCKER_USERNAME} --password ${DOCKER_PASSWORD}

docker tag shopifine-drools dmarjanovic/shopifine-drools:${VERSION}
docker push dmarjanovic/shopifine-drools:${VERSION}
docker tag shopifine-drools dmarjanovic/shopifine-drools:latest
docker push dmarjanovic/shopifine-drools:latest

# Remove Created Directory
rm -rf docker/drools-service
