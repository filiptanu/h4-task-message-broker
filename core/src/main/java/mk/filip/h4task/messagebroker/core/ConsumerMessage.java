package mk.filip.h4task.messagebroker.core;

import lombok.Data;

@Data
public class ConsumerMessage {

    private int messageId;
    private String body;

}