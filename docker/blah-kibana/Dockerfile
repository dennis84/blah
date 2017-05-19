FROM alpine:3.5

RUN apk upgrade --update && \
    apk add --update ca-certificates bash wget nodejs

RUN wget https://artifacts.elastic.co/downloads/kibana/kibana-5.4.0-linux-x86_64.tar.gz

RUN tar -xvzf kibana-5.4.0-linux-x86_64.tar.gz

RUN mkdir /opt

RUN mv kibana-5.4.0-linux-x86_64 /opt/kibana

RUN rm kibana-5.4.0-linux-x86_64.tar.gz

RUN rm -rf /opt/kibana/node/

RUN mkdir -p /opt/kibana/node/bin/

RUN ln -s $(which node) /opt/kibana/node/bin/node

ADD kibana.yml /opt/kibana/config/kibana.yml

ADD docker-entrypoint /docker-entrypoint

EXPOSE 5601

ENTRYPOINT ["/docker-entrypoint"]
