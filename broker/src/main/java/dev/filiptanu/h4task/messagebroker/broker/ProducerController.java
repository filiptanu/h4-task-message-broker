package dev.filiptanu.h4task.messagebroker.broker;

import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import dev.filiptanu.h4task.messagebroker.core.Message;

@RestController
public class ProducerController {

    private static Logger logger = LoggerFactory.getLogger(ProducerController.class);

    @Autowired
    private BrokerService brokerService;

    @PostMapping("/receiveProducerMessage")
    @ResponseStatus(value = HttpStatus.OK)
    public void receiveProducerMessage(@Valid @RequestBody Message message) {
        // TODO (filip): Add custom response for HttpMessageNotReadableException
        // TODO (filip): Add custom response for MethodArgumentNotValidException

        logger.info(message.toString());

        brokerService.processReceivedMessage(message.getBody());
    }

}