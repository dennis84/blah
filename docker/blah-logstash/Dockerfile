FROM blah/java

RUN apk upgrade --update && \
    apk add --update bash wget

RUN wget https://download.elastic.co/logstash/logstash/logstash-2.4.0.tar.gz

RUN tar -xvzf logstash-2.4.0.tar.gz

RUN mkdir /opt

RUN mv logstash-2.4.0 /opt/logstash

RUN rm logstash-2.4.0.tar.gz

ADD logstash.json /logstash.json

RUN /opt/logstash/bin/logstash-plugin install logstash-input-beats

EXPOSE 5044

ENTRYPOINT ["/opt/logstash/bin/logstash", "-f", "/logstash.conf"]
