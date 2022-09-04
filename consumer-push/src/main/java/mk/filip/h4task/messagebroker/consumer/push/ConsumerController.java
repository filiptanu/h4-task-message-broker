package mk.filip.h4task.messagebroker.consumer.push;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import mk.filip.h4task.messagebroker.core.ConfirmMessage;
import mk.filip.h4task.messagebroker.core.ConsumerMessage;

@RestController
public class ConsumerController {

    private static Logger logger = LoggerFactory.getLogger(ConsumerController.class);

    @Autowired
    private RestTemplate restTemplate;

    @Value("${consumer.id}")
    private String consumerId;
    @Value("${broker.confirm.message.endpoint}")
    private String brokerConfirmMessageEndpoint;
    @Value("${confirm.messages}")
    private boolean confirmMessages;

    @GetMapping(Config.HEALTHCHECK_ENDPOINT)
    @ResponseStatus(HttpStatus.OK)
    public void healthcheck() {}

    @PostMapping(Config.PUSH_CONSUMER_MESSAGE_ENDPOINT)
    public void pushConsumerMessage(@RequestBody ConsumerMessage consumerMessage) {
        logger.info("Message received: " + consumerMessage);

        if (confirmMessages) {
            ConfirmMessage confirmMessage = new ConfirmMessage();
            confirmMessage.setMessageId(consumerMessage.getMessageId());
            confirmMessage.setConsumerId(consumerId);

            ResponseEntity<Void> confirmMessageResponseEntity = restTemplate.postForEntity(brokerConfirmMessageEndpoint, confirmMessage, Void.class);

            logger.info("Message confirmed: " + consumerMessage);
            logger.info("Response code received from broker: " + confirmMessageResponseEntity.getStatusCode().toString());
        }
    }

}