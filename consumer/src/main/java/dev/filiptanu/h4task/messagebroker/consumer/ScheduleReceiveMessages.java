package dev.filiptanu.h4task.messagebroker.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduleReceiveMessages {

    @Autowired
    private ConsumerService consumerService;

    @Scheduled(fixedRateString = "${time.interval.milliseconds}", initialDelayString = "${time.interval.milliseconds}")
    public void receiveMessageFromBroker() {
        consumerService.consumeMessage();
    }

}