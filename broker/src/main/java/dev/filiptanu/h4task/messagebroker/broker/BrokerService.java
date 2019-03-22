package dev.filiptanu.h4task.messagebroker.broker;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import dev.filiptanu.h4task.messagebroker.core.ConsumerMessage;

@Service
public class BrokerService {

    @Autowired
    private BrokerRepository brokerRepository;

    public void processReceivedMessage(String body) {
        brokerRepository.insertMessage(body);

        // TODO (filip): if using push model, maybe notify the consumers that there is a new message
    }

    public Optional<ConsumerMessage> consumeMessage(String consumerId) {
        try {
            return Optional.of(brokerRepository.getFirstUnprocessedMessage(consumerId).toConsumerMessage());
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void confirmMessage(int messageId, String consumerId) {
        brokerRepository.confirmMessage(messageId, consumerId);
    }

    public void clearPendingMessages() {
        brokerRepository.clearPendingMessages();
    }

}