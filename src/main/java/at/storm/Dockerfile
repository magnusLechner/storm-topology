#
## docker-storm
#
# sentistorm/sentistorm
#
# BUILD: docker build -t eventstorm/sentistorm -f src/main/java/at/storm/Dockerfile .
#
# RUN (service) : docker run -p 3772:3772 -p 6379:6379 -p 8080:8080 -t -i eventstorm/sentistorm
#
FROM eventstorm/dockerstorm

RUN apt-get update
RUN apt-get install telnet
RUN apt-get install iputils-ping -y

# Overwrite Supervisor conf
ADD src/main/java/at/storm/supervisord.conf /etc/supervisor/conf.d/supervisord.conf

EXPOSE 3772
EXPOSE 6379
EXPOSE 8080

# Copy project
WORKDIR /storm-topology
ADD src/main/java/at/storm/entrypoint.sh /storm-topology
ADD src/main/java/at/storm/Dockerfile /storm-topology
ADD ./target/storm_sentiment_analysis-0.0.1-SNAPSHOT-jar-with-dependencies.jar /storm-topology

CMD ["/storm-topology/entrypoint.sh"]