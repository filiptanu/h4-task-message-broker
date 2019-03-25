package dev.filiptanu.h4task.messagebroker.broker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.dao.EmptyResultDataAccessException;
import dev.filiptanu.h4task.messagebroker.core.ConsumerMessage;

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

    @Test
    public void consumeMessage_repositoryReturnsEntity_shouldReturnOptionalWithEntity() {
        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setId(1);
        messageEntity.setBody("{\"body\": \"Some message...\"}");
        messageEntity.setReceivedFromProducer(LocalDateTime.now());
        messageEntity.setProcessingStatus(ProcessingStatus.NOT_PROCESSED);

        when(brokerRepository.getFirstUnprocessedMessage("1")).thenReturn(messageEntity);

        Optional<ConsumerMessage> consumerMessageOptional = brokerService.consumeMessage("1");

        assertTrue(consumerMessageOptional.isPresent());
        assertEquals(messageEntity.toConsumerMessage(), consumerMessageOptional.get());
        verify(brokerRepository, times(1)).getFirstUnprocessedMessage("1");
    }

    @Test
    public void consumeMessage_repositoryThrowsException_shouldReturnEmptyOptional() {
        when(brokerRepository.getFirstUnprocessedMessage("1")).thenThrow(EmptyResultDataAccessException.class);

        Optional<ConsumerMessage> consumerMessageOptional = brokerService.consumeMessage("1");

        assertFalse(consumerMessageOptional.isPresent());
        verify(brokerRepository, times(1)).getFirstUnprocessedMessage("1");
    }

    @Test
    public void confirmMessage() {
        brokerRepository.confirmMessage(1, "1");

        verify(brokerRepository, times(1)).confirmMessage(1, "1");
    }

    // TODO (filip): Add unit tests for this method
    @Test
    public void pushMessageToConsumer() {

    }

}