package mk.filip.h4task.messagebroker.core;

import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubscribeConsumerMessage {

    @NotNull
    private String consumerId;
    @NotNull
    private String healthcheckEndpoint;
    @NotNull
    private String pushEndpoint;

}