FROM openjdk:8-jdk-alpine
VOLUME /tmp
COPY target/*.jar consumer-pull.jar
ENTRYPOINT ["java", "-jar", "/consumer-pull.jar", "-Dconsumer.id=1"]