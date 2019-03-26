package dev.filiptanu.h4task.messagebroker.broker;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import dev.filiptanu.h4task.messagebroker.core.ConsumerMessage;
import dev.filiptanu.h4task.messagebroker.core.SubscribeConsumerMessage;

@Service
public class BrokerService {

    private static Logger logger = LoggerFactory.getLogger(ProducerController.class);

    @Autowired
    private BrokerRepository brokerRepository;

    @Autowired
    private List<SubscribeConsumerMessage> consumers;

    @Autowired
    private RestTemplate restTemplate;

    private int nextConsumerIndex = -1;

    public void processReceivedMessage(String body) {
        logger.info("Processing received message: " + body);

        brokerRepository.insertMessage(body);
    }

    public Optional<ConsumerMessage> consumeMessage(String consumerId) {
        logger.info("Trying to get a message for consumer with consumerId: " + consumerId);
        try {
            return Optional.of(brokerRepository.getFirstUnprocessedMessage(consumerId).toConsumerMessage());
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void confirmMessage(int messageId, String consumerId) {
        logger.info("Confirming a message with messageId: " + messageId + " and consumerId: " + consumerId);
        brokerRepository.confirmMessage(messageId, consumerId);
    }

    public void clearPendingMessages() {
        logger.info("Clear pending messages...");
        brokerRepository.clearPendingMessages();
    }

    public void addConsumer(SubscribeConsumerMessage subscribeConsumerMessage) {
        logger.info("Adding a new consumer: " +subscribeConsumerMessage);

        consumers.add(subscribeConsumerMessage);
    }

    public synchronized void pushMessagesToConsumers() {
        logger.info("Pushing messages to consumers...");
        while (true) {
            try {
                pushMessageToConsumer();
            } catch (NoRegisteredConsumersException | NoMessagesPresentAtBrokerException e) {
                logger.error(e.getMessage());

                break;
            }
        }
    }

    public void pushMessageToConsumer() {
        logger.info("Pushing a message to a consumer...");

        SubscribeConsumerMessage subscribeConsumerMessage = getNextConsumer();
        Optional<ConsumerMessage> consumerMessageOptional = consumeMessage(subscribeConsumerMessage.getConsumerId());

        if (consumerMessageOptional.isPresent()) {
            ConsumerMessage consumerMessage = consumerMessageOptional.get();

            logger.info("Consumer: " + subscribeConsumerMessage);
            logger.info("Message: " + consumerMessage);

            try {
                restTemplate.postForEntity(subscribeConsumerMessage.getPushEndpoint(), consumerMessageOptional.get(), Void.class);
            } catch (RestClientException e) {
                logger.error("Communicating with the consumer with id: " + subscribeConsumerMessage.getConsumerId() + " failed: " + e.getMessage());

                removeConsumer(subscribeConsumerMessage);
            }
        } else {
            decrementConsumerIndex();
            throw new NoMessagesPresentAtBrokerException("No more messages currently present at the broker...");
        }
    }

    private SubscribeConsumerMessage getNextConsumer() {
        logger.info("Getting next consumer index...");

        synchronized (consumers) {
            if (consumers.size() <= 0) {
                nextConsumerIndex = -1;
                throw new NoRegisteredConsumersException("There are no registered consumers to send messages to...");
            }

            nextConsumerIndex = (nextConsumerIndex + 1) % consumers.size();

            logger.info("Next consumer index: " + nextConsumerIndex);

            return consumers.get(nextConsumerIndex);
        }
    }

    private void decrementConsumerIndex() {
        logger.info("Decrementing next consumer index...");

        nextConsumerIndex--;
    }

    private void removeConsumer(SubscribeConsumerMessage subscribeConsumerMessage) {
        logger.info("Removing consumer: " + subscribeConsumerMessage);

        consumers.remove(subscribeConsumerMessage);
    }

    public void removeInactiveConsumers() {
        logger.info("Removing inactive consumers...");

        synchronized (consumers) {
            Iterator<SubscribeConsumerMessage> i = consumers.iterator();

            while (i.hasNext()) {
                SubscribeConsumerMessage subscribeConsumerMessage = i.next();

                try {
                    restTemplate.getForEntity(subscribeConsumerMessage.getHealthcheckEndpoint(), Void.class);
                } catch (RestClientException e) {
                    logger.error("Healthcheck failed for the consumer with id: " + subscribeConsumerMessage.getConsumerId());

                    removeConsumer(subscribeConsumerMessage);
                }
            }
        }
    }

}