package dev.filiptanu.h4task.messagebroker.broker;

import java.util.Optional;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import dev.filiptanu.h4task.messagebroker.core.ConfirmMessage;
import dev.filiptanu.h4task.messagebroker.core.ConsumerMessage;

@RestController
public class ConsumerController {

    private static Logger logger = LoggerFactory.getLogger(ProducerController.class);

    @Autowired
    private BrokerService brokerService;

    @GetMapping("/sendConsumerMessage")
    public ResponseEntity<ConsumerMessage> sendConsumerMessage(@RequestParam String consumerId) {
        Optional<ConsumerMessage> messageOptional = brokerService.consumeMessage(consumerId);

        if (messageOptional.isPresent()) {
            logger.info("Sending message to consumer with id: " + consumerId);
            return ResponseEntity.ok(messageOptional.get());
        }

        logger.info("No messages currently present in the database. Nothing to send to consumer with id: " + consumerId);
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/confirmMessage")
    @ResponseStatus(HttpStatus.OK)
    public void confirmMessageReceived(@Valid @RequestBody ConfirmMessage consumerMessage) {
        logger.info(consumerMessage.toString());
        brokerService.confirmMessage(consumerMessage.getMessageId(), consumerMessage.getConsumerId());
    }

    // TODO (filip): Try implementing a push-based message delivery

}