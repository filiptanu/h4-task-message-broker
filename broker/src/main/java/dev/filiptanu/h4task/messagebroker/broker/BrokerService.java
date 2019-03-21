package dev.filiptanu.h4task.messagebroker.broker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BrokerService {

    @Autowired
    private BrokerRepository brokerRepository;

    public void processReceivedMessage(String body) {
        brokerRepository.insertMessage(body);

        // TODO (filip): if using push model, maybe notify the consumers that there is a new message
    }

}