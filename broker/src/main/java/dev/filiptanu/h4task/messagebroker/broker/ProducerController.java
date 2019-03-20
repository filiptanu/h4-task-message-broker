package dev.filiptanu.h4task.messagebroker.broker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import dev.filiptanu.h4task.messagebroker.core.Message;

@RestController
public class ProducerController {

    private static Logger logger = LoggerFactory.getLogger(ProducerController.class);

    @PostMapping("/receiveProducerMessage")
    @ResponseStatus(value = HttpStatus.OK)
    public void receiveProducerMessage(@RequestBody Message message) {
        // TODO (filip): Add custom response for HttpMessageNotReadableException

        logger.info(message.toString());
    }

}