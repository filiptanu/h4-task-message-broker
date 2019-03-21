package dev.filiptanu.h4task.messagebroker.broker;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import dev.filiptanu.h4task.messagebroker.core.Message;

@Service
public class BrokerService {

    @Autowired
    private BrokerRepository brokerRepository;

    public void processReceivedMessage(String body) {
        brokerRepository.insertMessage(body);

        // TODO (filip): if using push model, maybe notify the consumers that there is a new message
    }

    public Optional<Message> consumeMessage() {
        try {
            return Optional.of(brokerRepository.getFirstUnprocessedMessage().toMessage());
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

}