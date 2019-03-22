package dev.filiptanu.h4task.messagebroker.producer;

import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import dev.filiptanu.h4task.messagebroker.core.ProducerMessage;

@Service
public class ProducerService {

    private static Logger logger = LoggerFactory.getLogger(ProducerService.class);

    @Autowired
    private RestTemplate restTemplate;

    @Value("${producer.id}")
    private String producerId;
    @Value("${broker.produce.message.endpoint}")
    private String brokerProduceMessageEndpoint;

    public void produceMessage() {
        logger.info("Producing a new message...");
        ProducerMessage producerMessage = new ProducerMessage();
        Random random = new Random();
        producerMessage.setBody("Producer " + producerId + " - " + random.nextInt(10000));

        logger.info("Sending message: " + producerMessage.toString());
        ResponseEntity<Void> responseEntity = restTemplate.postForEntity(brokerProduceMessageEndpoint, producerMessage, Void.class);

        logger.info("Response code received from broker: " + responseEntity.getStatusCode().toString());
    }

}