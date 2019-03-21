package dev.filiptanu.h4task.messagebroker.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduleSendMessages {

    @Autowired
    private ProducerService producerService;

    @Scheduled(fixedRateString = "${time.interval.milliseconds}", initialDelayString = "${time.interval.milliseconds}")
    public void sendNewMessage() {
        producerService.produceMessage();
    }

}