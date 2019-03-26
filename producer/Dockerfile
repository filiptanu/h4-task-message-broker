FROM openjdk:8-jdk-alpine
VOLUME /tmp
COPY target/*.jar producer.jar
ENTRYPOINT ["java", "-jar", "/producer.jar", "-Dproducer.id=1"]