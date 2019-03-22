package dev.filiptanu.h4task.messagebroker.broker;

import java.time.LocalDateTime;
import lombok.Data;
import dev.filiptanu.h4task.messagebroker.core.ConsumerMessage;

@Data
public class MessageEntity {

    private int id;
    private String body;
    private LocalDateTime receivedFromProducer;
    private ProcessingStatus processingStatus;
    private String consumerId;
    private LocalDateTime sentToConsumer;
    private LocalDateTime confirmedFromConsumer;

    public ConsumerMessage toConsumerMessage() {
        ConsumerMessage consumerMessage = new ConsumerMessage();
        consumerMessage.setMessageId(id);
        consumerMessage.setBody(body);

        return consumerMessage;
    }

}