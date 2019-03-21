package dev.filiptanu.h4task.messagebroker.broker;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import java.time.LocalDateTime;
import lombok.Data;
import dev.filiptanu.h4task.messagebroker.core.Message;

@Data
public class MessageEntity {

    private int id;
    private String body;
    private LocalDateTime received;
    @JsonProperty(access = Access.WRITE_ONLY)
    private boolean processed;

    public Message toMessage() {
        Message message = new Message();
        message.setBody(body);

        return message;
    }

}