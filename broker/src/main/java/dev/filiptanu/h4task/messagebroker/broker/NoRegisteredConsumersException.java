package dev.filiptanu.h4task.messagebroker.broker;

public class NoRegisteredConsumersException extends RuntimeException {

    public NoRegisteredConsumersException(String message) {
        super(message);
    }

}