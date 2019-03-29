FROM openjdk:8-jdk-alpine
COPY target/producer-1.0-SNAPSHOT.jar producer.jar
ENTRYPOINT ["java", "-jar", "/producer.jar"]