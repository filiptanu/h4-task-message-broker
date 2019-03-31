# H4 Task - Message Broker

This project is a Java implementation of a message broker, following the guidelines from [the hfour exercises/message-broker repo](https://github.com/hfour/exercises/tree/master/message-broker).

The project contains the following modules:

* core
* broker
* producer
* consumer-pull
* consumer-push
* consumer-push-node

### core

This module containing classes used by the other modules.

### broker

A broker is a program that can receive messages from producers and send the messages it receives to consumers.

Once the broker receives a message, it stores it in a database before sending it to a consumer.

That means that a producer can send a message to the broker even when there are no consumers available.

The broker delivers a message to a consumer at most once. One message will not be sent to multiple consumers.

The consumer must confirm to the broker that it received a message.

If a consumer does not confirm it received a message in a given time period, after that period is passed the broker will treat the message as if it was not sent at all, and send it to the next available consumer.

There are two types of consumers that can connect to the broker:

* pull-based consumer
* push-based consumer

### producer

A producer is a program that sends messages to the broker.

### consumer-pull

A pull-based consumer will ask the broker for new messages at a regular interval.

### consumer-push

A push-based consumer will subscribe to the broker, and the broker will push any message it receives to the subscribed consumers in a Round-robin fashion.

### consumer-push-node

A Node.js implementation of a push-based consumer.

## Prerequisites

In order to be able to build the project, the following dependencies are required to be installed:

* Java 8
* Maven 3
* Docker
* PostgreSQL (if you do want to run the project without Docker)
* Node.js (if you do want to run the project without Docker)

## Build

In a terminal, navigate to the project root directory and run the following command to build the project:

```
mvn clean install
```

The above command will download any project dependencies, run tests, build the modules' artifacts and build Docker images for every module.

Note that in order for the above command to build the Docker images, the current user needs to have permissions to run Docker withoud ```sudo```.

To build the ```consumer-push-node``` module (without using Docker), run the following commands:

```
cd consumer-push-node
npm install
```

## Deployment

Before running any of the built artifacts you need to have PostgreSQL up and running.

The following sections will explain how to run the separate modules.

The commands can be run from the root project directory.

The commands will be shown only with the required JVM arguments.

If you want to customize the behaviour of any module, you can pass additional JVM arguments (listed in each module's section below).

For example:

```
java -jar broker/target/broker-1.0-SNAPSHOT.jar -Dserver.port=9999
```

You can see the default values of each JVM argument in the appropriate application.properties file, located in:

```
<module_folder>/src/main/resources/application.properties
``` 

#### broker

You can run the broker with the following command:

```
java -jar broker/target/broker-1.0-SNAPSHOT.jar
```

You can use the following JVM arguments when running the broker:

* server.port - the port on which the broker will listen
* spring.datasource.url - a URL for connecting with the database
* spring.datasource.username - the database username 
* spring.datasource.password - the database password
* clear.pending.messages.time.interval.milliseconds - A repeating time interval after which the broker will clear any pending messages (messages not confirmed by consumers)
* clear.inactive.consumers.time.interval.milliseconds - A repeating time interval after which the broker will ping the healthcheck endpoints of any subscribed consumers, and will remove any consumer that will not respond with HttpStatusCode == 200

#### producer

You can run a producer with the following command:

```
java -jar producer/target/producer-1.0-SNAPSHOT.jar -Dproducer.id=1
```

You must specify a producer.id when running a producer.

You can use the following JVM arguments when running a producer:

* time.interval.milliseconds - a repeating time interval after which this producer will send a new message to the broker
* producer.id - this producer's id (mandatory)
* broker.produce.message.endpoint - an endpoint on which this producer will send messages to the broker

#### consumer-pull

You can run a pull-based consumer with the following command:

```
java -jar consumer-pull/target/consumer-pull-1.0-SNAPSHOT.jar -Dconsumer.id=1
```

You must specify a consumer.id when running a pull-based consumer.

You can use the following JVM arguments when running a pull-based consumer:

* time.interval.milliseconds - a repeating time interval after which this consumer will ask for a new message from the broker
* consumer.id - this consumer's id (mandatory)
* broker.consume.message.endpoint - an endpoint on which this consumer will ask for new messages from the broker
* broker.confirm.message.endpoint - an endpoint on which this consumer will confirm messages it received to the broker
* confirm.messages - a flag controlling whether or not this consumer will confirm the messages it receives to the broker

#### consumer-push

You can run a push-based consumer with the following command:

```
java -jar consumer-push/target/consumer-push-1.0-SNAPSHOT.jar -Dconsumer.id=1
```

You can use the following JVM arguments when running a push-based consumer:

* server.port - the server port on which this consumer will listen for healthchecks or pushed messages
* consumer.id - this consumer's id (mandatory)
* broker.subscribe.endpoint - an endpoint on which this consumer will subscribe to the broker
* broker.confirm.message.endpoint - an endpoint on which this consumer will confirm messages it received to the broker
* confirm.messages - a flag controlling whether or not this consumer will confirm the messages it receives to the broker

#### consumer-push-node

You can run a push-based consumer implemented in node with the following command:

```
cd consumer-push-node && CONSUMER_ID=1 npm start
```

You can use the following environment variables when running a push-based consumer:

* PORT - the server port on which this consumer will listen for healthchecks or pushed messages
* CONSUMER_ID - this consumer's id (mandatory)
* BROKER_SUBSCRIBE_ENDPOINT - an endpoint on which this consumer will subscribe to the broker
* BROKER_CONFIRM_MESSAGE_ENDPOINT - an endpoint on which this consumer will confirm messages it received to the broker
* CONFIRM_MESSAGES - a flag controlling whether or not this consumer will confirm the messages it receives to the broker

### Example of a deployment of a broker, 2 producers and 4 push-based consumers

Run each command in a separate terminal to see the output of the interactions between the components.

```
java -jar broker/target/broker-1.0-SNAPSHOT.jar
java -jar producer/target/producer-1.0-SNAPSHOT.jar -Dproducer.id=1
java -jar producer/target/producer-1.0-SNAPSHOT.jar -Dproducer.id=2 -Dtime.interval.milliseconds=3000
java -jar consumer-push/target/consumer-push-1.0-SNAPSHOT.jar -Dconsumer.id=1
java -jar consumer-push/target/consumer-push-1.0-SNAPSHOT.jar -Dconsumer.id=2 -Dserver.port=8082
java -jar consumer-push/target/consumer-push-1.0-SNAPSHOT.jar -Dconsumer.id=3 -Dserver.port=8083 -Dconfirm.messages=false
cd consumer-push-node && PORT=8084 CONSUMER_ID=4 BROKER_SUBSCRIBE_ENDPOINT=http://localhost:8080/subscribe BROKER_CONFIRM_MESSAGE_ENDPOINT=http://localhost:8080/confirmMessage npm start
```

## Deployment with Docker

When building the project with Maven, Docker images are automatically created and stored locally.

You can view the Docker images using the following command:

```
docker image ls
```

The following Docker images should be listed:

* h4-task-message-broker/broker
* h4-task-message-broker/producer
* h4-task-message-broker/consumer-pull
* h4-task-message-broker/consumer-push
* h4-task-message-broker/consumer-push-node

### Example of a deployment (using Docker) of a broker, 2 producers and 4 push-based consumers

```
docker network create broker-network

docker run --name message-broker-postgres -e POSTGRES_USER=message-broker -e POSTGRES_PASSWORD=message-broker-password -p 5432:5432 --network broker-network -d postgres:latest

docker run --name broker --network broker-network -e spring.datasource.url=jdbc:postgresql://message-broker-postgres:5432/message-broker h4-task-message-broker/broker:1.0-SNAPSHOT
docker run --name producer-1 --network broker-network -e producer.id=1 -e broker.produce.message.endpoint=http://broker:8080/receiveProducerMessage h4-task-message-broker/producer:1.0-SNAPSHOT
docker run --name producer-2 --network broker-network -e producer.id=2 -e time.interval.milliseconds=3000 -e broker.produce.message.endpoint=http://broker:8080/receiveProducerMessage h4-task-message-broker/producer:1.0-SNAPSHOT
docker run --name consumer-push-1 --network broker-network -e consumer.id=1 -e broker.subscribe.endpoint=http://broker:8080/subscribe -e broker.confirm.message.endpoint=http://broker:8080/confirmMessage h4-task-message-broker/consumer-push:1.0-SNAPSHOT
docker run --name consumer-push-2 --network broker-network -e consumer.id=2 -e server.port=8082 -e broker.subscribe.endpoint=http://broker:8080/subscribe -e broker.confirm.message.endpoint=http://broker:8080/confirmMessage h4-task-message-broker/consumer-push:1.0-SNAPSHOT
docker run --name consumer-push-3 --network broker-network -e consumer.id=3 -e server.port=8083 -e confirm.messages=false -e broker.subscribe.endpoint=http://broker:8080/subscribe -e broker.confirm.message.endpoint=http://broker:8080/confirmMessage h4-task-message-broker/consumer-push:1.0-SNAPSHOT
docker run --name consumer-push-node-1 --network broker-network -e CONSUMER_ID=4 -e PORT=8084 -e BROKER_SUBSCRIBE_ENDPOINT=http://broker:8080/subscribe -e BROKER_CONFIRM_MESSAGE_ENDPOINT=http://broker:8080/confirmMessage h4-task-message-broker/consumer-push-node:1.0-SNAPSHOT
```

## Deployment with Docker Compose

If you have Docker Compose installed, you can run the previous setup with the following command:

```
docker-compose up
```