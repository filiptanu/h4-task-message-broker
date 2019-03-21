package dev.filiptanu.h4task.messagebroker.broker;

import static org.junit.Assert.*;

import org.junit.Test;
import dev.filiptanu.h4task.messagebroker.core.Message;

public class MessageEntityTest {

    @Test
    public void toMessage() {
        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setBody("Some message...");

        Message message = new Message();
        message.setBody("Some message...");

        assertEquals(message, messageEntity.toMessage());
    }

}