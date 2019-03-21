package dev.filiptanu.h4task.messagebroker.broker;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class BrokerServiceTest {

    @InjectMocks
    @Spy
    private BrokerService brokerService;

    @Mock
    private BrokerRepository brokerRepository;

    @Test
    public void processReceivedMessage() {
        brokerService.processReceivedMessage("Some message...");

        verify(brokerRepository, times(1)).insertMessage("Some message...");
    }

}