FROM java:8

ADD target/imustacm-problem-service-1.0-SNAPSHOT.jar /server/app.jar
WORKDIR /server
ENV JAVA_OPTS=""
EXPOSE 9090
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /server/app.jar" ]