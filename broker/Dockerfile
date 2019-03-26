FROM openjdk:8-jdk-alpine
VOLUME /tmp
COPY target/*.jar broker.jar
ENTRYPOINT ["java", "-jar", "/broker.jar"]