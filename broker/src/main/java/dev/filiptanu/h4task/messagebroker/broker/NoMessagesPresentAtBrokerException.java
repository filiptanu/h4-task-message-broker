package dev.filiptanu.h4task.messagebroker.broker;

public class NoMessagesPresentAtBrokerException extends RuntimeException {

    public NoMessagesPresentAtBrokerException(String message) {
        super(message);
    }

}