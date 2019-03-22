package dev.filiptanu.h4task.messagebroker.core;

import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProducerMessage {

    @NotNull
    private String body;

}