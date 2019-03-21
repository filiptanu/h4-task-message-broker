# H4 Task - Message Broker
TODO (filip): Add content to the README.md file.
For now, it is a placeholder file containing commands for running the separate applications and
anything else related to them.

docker run --name message-broker-postgres -e POSTGRES_USER=message-broker -e POSTGRES_PASSWORD=message-broker-password -p 5432:5432 -d postgres

java -jar broker-1.0-SNAPSHOT.jar

java -jar producer-1.0-SNAPSHOT.jar -Dproducer.id=1

java -jar producer-1.0-SNAPSHOT.jar -Dproducer.id=2 -Dtime.interval.milliseconds=3000

java -jar consumer-1.0-SNAPSHOT.jar -consumer.id=1

java -jar consumer-1.0-SNAPSHOT.jar -consumer.id=2 -Dtime.interval.milliseconds=3000