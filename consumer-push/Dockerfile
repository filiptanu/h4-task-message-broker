FROM openjdk:8-jdk-alpine
COPY target/consumer-push-1.0-SNAPSHOT.jar consumer-push.jar
ENTRYPOINT ["java", "-jar", "/consumer-push.jar"]