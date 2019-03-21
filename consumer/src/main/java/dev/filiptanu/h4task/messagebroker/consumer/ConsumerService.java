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
import dev.filiptanu.h4task.messagebroker.core.Message;

@Service
public class ConsumerService {

    private static Logger logger = LoggerFactory.getLogger(ConsumerService.class);

    @Autowired
    private RestTemplate restTemplate;

    @Value("${consumer.id}")
    private String consumerId;
    @Value("${broker.endpoint}")
    private String brokerEndpoint;

    public void consumeMessage() {
        logger.info("Consuming a message from the broker...");

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(brokerEndpoint)
                .queryParam("id", consumerId);

        try {
            ResponseEntity<Message> responseEntity = restTemplate.getForEntity(builder.toUriString(), Message.class);

            logger.info("Response code received from broker: " + responseEntity.getStatusCode().toString());
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                logger.info("Message received: " + responseEntity.getBody());
            }
        } catch (HttpClientErrorException e) {
            logger.info("The broker contains no messages that can be processed at this time...");
        } catch (RestClientException e) {
            logger.error("Communicating with the broker failed:");
            logger.error(e.getMessage());
        }
    }

}