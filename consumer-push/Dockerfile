FROM openjdk:8-jdk-alpine
VOLUME /tmp
COPY target/*.jar consumer-push.jar
ENTRYPOINT ["java", "-jar", "/consumer-push.jar", "-Dconsumer.id=1"]