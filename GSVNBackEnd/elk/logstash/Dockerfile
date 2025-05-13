FROM docker.elastic.co/logstash/logstash:8.12.2

# install dependency
RUN /usr/share/logstash/bin/logstash-plugin install logstash-input-jdbc
RUN /usr/share/logstash/bin/logstash-plugin install logstash-filter-mutate

COPY ./elk/logstash/driver/mysql-connector-java-8.0.11.jar /usr/share/logstash/logstash-core/lib/jars/mysql-connector-java.jar
