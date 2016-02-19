FROM blah/java

RUN wget https://download.elastic.co/elasticsearch/elasticsearch/elasticsearch-2.2.0.tar.gz

RUN tar -xvzf elasticsearch-2.2.0.tar.gz

RUN mv elasticsearch-2.2.0 /usr/share/elasticsearch

RUN rm elasticsearch-2.2.0.tar.gz

RUN mkdir /usr/share/elasticsearch/data

ADD elasticsearch.yml /usr/share/elasticsearch/config/elasticsearch.yml

RUN adduser -S elasticsearch

RUN chown -R elasticsearch /usr/share/elasticsearch

VOLUME "/usr/share/elasticsearch/data"

EXPOSE 9200 9300

USER elasticsearch

CMD ["/usr/share/elasticsearch/bin/elasticsearch"]
