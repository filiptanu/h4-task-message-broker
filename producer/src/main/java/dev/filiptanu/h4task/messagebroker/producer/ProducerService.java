package dev.filiptanu.h4task.messagebroker.producer;

import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import dev.filiptanu.h4task.messagebroker.core.Message;

@Service
public class ProducerService {

    private static Logger logger = LoggerFactory.getLogger(ProducerService.class);

    @Autowired
    private RestTemplate restTemplate;

    @Value("${producer.id}")
    private String producerId;
    @Value("${broker.endpoint}")
    private String brokerEndpoint;

    public void produceMessage() {
        logger.info("Producing a new message...");
        Message message = new Message();
        Random random = new Random();
        message.setBody("Producer " + producerId + " - " + random.nextInt(10000));

        logger.info("Sending message: " + message.toString());
        ResponseEntity<Void> responseEntity = restTemplate.postForEntity(brokerEndpoint, message, Void.class);

        logger.info("Response code received from broker: " + responseEntity.getStatusCode().toString());
    }

}