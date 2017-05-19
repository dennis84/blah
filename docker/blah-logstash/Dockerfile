FROM blah/java

RUN apk upgrade --update && \
    apk add --update bash wget

RUN wget https://artifacts.elastic.co/downloads/logstash/logstash-5.4.0.tar.gz

RUN tar -xvzf logstash-5.4.0.tar.gz

RUN mkdir /opt

RUN mv logstash-5.4.0 /opt/logstash

RUN rm logstash-5.4.0.tar.gz

ADD logstash.conf /logstash.conf

RUN /opt/logstash/bin/logstash-plugin install logstash-input-beats

EXPOSE 5044

ADD docker-entrypoint /docker-entrypoint

ENTRYPOINT ["/docker-entrypoint", "-f", "/logstash.conf"]
