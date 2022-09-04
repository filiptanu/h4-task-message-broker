package mk.filip.h4task.messagebroker.broker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduleTasksService {

    private static Logger logger = LoggerFactory.getLogger(ScheduleTasksService.class);

    @Autowired
    private BrokerService brokerService;

    @Scheduled(fixedRateString = "${clear.pending.messages.time.interval.milliseconds}", initialDelayString = "${clear.pending.messages.time.interval.milliseconds}")
    public void clearPendingMessages() {
        logger.info("Clearing messages with processing_status = \"PENDING\"...");

        brokerService.clearPendingMessages();
    }

    // TODO (filip): Remove consumers that do not return 200 on the healthcheck
//    @Scheduled
    public void removeInactiveConsumers() {

    }

}