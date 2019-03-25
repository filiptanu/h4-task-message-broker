package dev.filiptanu.h4task.messagebroker.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import dev.filiptanu.h4task.messagebroker.core.ConfirmMessage;
import dev.filiptanu.h4task.messagebroker.core.ConsumerMessage;

@Service
public class ConsumerService {

    private static Logger logger = LoggerFactory.getLogger(ConsumerService.class);

    @Autowired
    private RestTemplate restTemplate;

    @Value("${consumer.id}")
    private String consumerId;
    @Value("${broker.consume.message.endpoint}")
    private String brokerConsumeMessageEndpoint;
    @Value("${broker.confirm.message.endpoint}")
    private String brokerConfirmMessageEndpoint;
    @Value("${confirm.messages}")
    private boolean confirmMessages;

    public void consumeMessage() {
        logger.info("Consuming a message from the broker...");

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(brokerConsumeMessageEndpoint)
                .queryParam("consumerId", consumerId);

        try {
            ResponseEntity<ConsumerMessage> consumerMessageResponseEntity = restTemplate.getForEntity(builder.toUriString(), ConsumerMessage.class);

            logger.info("Response code received from broker: " + consumerMessageResponseEntity.getStatusCode().toString());
            if (consumerMessageResponseEntity.getStatusCode() == HttpStatus.OK) {
                ConsumerMessage consumerMessage = consumerMessageResponseEntity.getBody();
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
        } catch (HttpClientErrorException e) {
            logger.info("The broker contains no messages that can be processed at this time...");
        } catch (RestClientException e) {
            logger.error("Communicating with the broker failed: " + e.getMessage());
        }
    }

}