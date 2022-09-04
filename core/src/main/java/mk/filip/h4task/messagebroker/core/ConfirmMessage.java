package mk.filip.h4task.messagebroker.core;

import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ConfirmMessage {

    @NotNull
    private int messageId;
    @NotNull
    private String consumerId;

}