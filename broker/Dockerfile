FROM openjdk:8-jdk-alpine
COPY target/broker-1.0-SNAPSHOT.jar broker.jar
ENTRYPOINT ["java", "-jar", "/broker.jar"]