package dev.filiptanu.h4task.messagebroker.broker;

import static org.junit.Assert.*;

import java.time.LocalDateTime;
import org.junit.Test;
import dev.filiptanu.h4task.messagebroker.core.ConsumerMessage;
import dev.filiptanu.h4task.messagebroker.core.ProducerMessage;

public class MessageEntityTest {

    @Test
    public void toMessage() {
        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setId(1);
        messageEntity.setBody("Some message...");

        ConsumerMessage consumerMessageExpected = new ConsumerMessage();
        consumerMessageExpected.setMessageId(1);
        consumerMessageExpected.setBody("Some message...");

        ConsumerMessage consumerMessageActual = messageEntity.toConsumerMessage();

        assertEquals(consumerMessageExpected, consumerMessageActual);
    }

}