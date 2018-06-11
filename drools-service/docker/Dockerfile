FROM anapsix/alpine-java:8_server-jre
LABEL maintainer="Dragutin Marjanovic <dragutin@lambdaworks.io>"

EXPOSE 9000
WORKDIR /opt

COPY ./docker-entrypoint.sh /usr/local/bin
COPY ./drools-service .

RUN addgroup -S shopifine \
    && adduser -SG shopifine shopifine \
    && apk add --no-cache 'su-exec>=0.2' \
    && ln -s /opt/bin/drools-service /usr/local/bin/drools-service

ENTRYPOINT [ "docker-entrypoint.sh" ]
CMD [ "drools-service" ]
