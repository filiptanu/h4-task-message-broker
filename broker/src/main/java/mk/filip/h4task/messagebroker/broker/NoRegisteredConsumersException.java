package mk.filip.h4task.messagebroker.broker;

public class NoRegisteredConsumersException extends RuntimeException {

    public NoRegisteredConsumersException(String message) {
        super(message);
    }

}