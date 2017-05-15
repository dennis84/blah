FROM alpine:edge

RUN echo http:"//dl-cdn.alpinelinux.org/alpine/edge/testing" >> /etc/apk/repositories 

RUN apk upgrade --update && \
    apk add --update bash cargo
