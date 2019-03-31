package dev.filiptanu.h4task.messagebroker.consumer.push;

import static dev.filiptanu.h4task.messagebroker.consumer.push.Config.HEALTHCHECK_ENDPOINT;
import static dev.filiptanu.h4task.messagebroker.consumer.push.Config.PUSH_CONSUMER_MESSAGE_ENDPOINT;

import java.net.InetAddress;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import dev.filiptanu.h4task.messagebroker.core.SubscribeConsumerMessage;

@Service
public class SubscribeService {

    private static Logger logger = LoggerFactory.getLogger(SubscribeService.class);

    @Autowired
    private RestTemplate restTemplate;

    @Value("${server.port}")
    private String serverPort;
    @Value("${consumer.id}")
    private String consumerId;
    @Value("${broker.subscribe.endpoint}")
    private String brokerSubscribeEndpoint;

    @EventListener(ApplicationReadyEvent.class)
    @SneakyThrows
    public void subscribeAtBroker() {
        logger.info("Subscribing at broker...");

        String hostName = InetAddress.getLocalHost().getHostName();

        String healthcheckEndpoint = "http://" + hostName + ":" + serverPort + HEALTHCHECK_ENDPOINT;
        String pushEndpoint = "http://" + hostName + ":" + serverPort + PUSH_CONSUMER_MESSAGE_ENDPOINT;

        SubscribeConsumerMessage subscribeConsumerMessage = new SubscribeConsumerMessage();
        subscribeConsumerMessage.setConsumerId(consumerId);
        subscribeConsumerMessage.setHealthcheckEndpoint(healthcheckEndpoint);
        subscribeConsumerMessage.setPushEndpoint(pushEndpoint);

        ResponseEntity<Void> confirmMessageResponseEntity = restTemplate.postForEntity(brokerSubscribeEndpoint, subscribeConsumerMessage, Void.class);
        if (confirmMessageResponseEntity.getStatusCode() == HttpStatus.OK) {
            logger.info("Successfully registered at broker...");
        } else {
            logger.error("There was a problem with registering at the broker... Shutting down the consumer...");
            System.exit(-1);
        }
    }

}