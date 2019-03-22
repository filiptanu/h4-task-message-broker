package dev.filiptanu.h4task.messagebroker.broker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduleClearPendingMessages {

    private static Logger logger = LoggerFactory.getLogger(ScheduleClearPendingMessages.class);

    @Autowired
    private BrokerService brokerService;

    @Scheduled(fixedRateString = "${clear.pending.messages.time.interval.milliseconds}", initialDelayString = "${clear.pending.messages.time.interval.milliseconds}")
    public void clearPendingMessages() {
        logger.info("Clearing messages with processing_status = \"PENDING\"...");

        brokerService.clearPendingMessages();
    }

}