FROM alpine:3.4

RUN apk upgrade --update && \
    apk add --update ca-certificates bash wget nodejs

RUN wget https://download.elastic.co/kibana/kibana/kibana-4.6.1-linux-x86_64.tar.gz

RUN tar -xvzf kibana-4.6.1-linux-x86_64.tar.gz

RUN mkdir /opt

RUN mv kibana-4.6.1-linux-x86_64 /opt/kibana

RUN rm kibana-4.6.1-linux-x86_64.tar.gz

RUN rm -rf /opt/kibana/node/

RUN mkdir -p /opt/kibana/node/bin/

RUN ln -s $(which node) /opt/kibana/node/bin/node

ADD kibana.yml /opt/kibana/config/kibana.yml

ADD docker-entrypoint /docker-entrypoint

EXPOSE 5601

ENTRYPOINT ["/docker-entrypoint"]
