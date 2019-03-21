package dev.filiptanu.h4task.messagebroker.broker;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import dev.filiptanu.h4task.messagebroker.core.Message;

@RestController
public class ConsumerController {

    private static Logger logger = LoggerFactory.getLogger(ProducerController.class);

    @Autowired
    private BrokerService brokerService;

    @GetMapping("/sendConsumerMessage")
    public ResponseEntity<Message> sendConsumerMessage(@RequestParam int id) {
        Optional<Message> messageOptional = brokerService.consumeMessage();

        if (messageOptional.isPresent()) {
            logger.info("Sending message to consumer with id: " + id);
            return ResponseEntity.ok(messageOptional.get());
        }

        logger.info("No messages currently present in the database. Nothing to send to consumer with id: " + id);
        return ResponseEntity.notFound().build();
    }

    // TODO (filip): Try implementing a push-based message delivery

}