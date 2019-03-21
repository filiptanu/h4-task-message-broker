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
import dev.filiptanu.h4task.messagebroker.core.Message;

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
        messageEntity.setReceived(LocalDateTime.now());
        messageEntity.setProcessed(true);

        when(brokerRepository.getFirstUnprocessedMessage()).thenReturn(messageEntity);

        Optional<Message> messageOptional = brokerService.consumeMessage();

        assertTrue(messageOptional.isPresent());
        assertEquals(messageEntity.toMessage(), messageOptional.get());
        verify(brokerRepository, times(1)).getFirstUnprocessedMessage();
    }

    @Test
    public void consumeMessage_repositoryThrowsException_shouldReturnEmptyOptional() {
        when(brokerRepository.getFirstUnprocessedMessage()).thenThrow(EmptyResultDataAccessException.class);

        Optional<Message> messageOptional = brokerService.consumeMessage();

        assertFalse(messageOptional.isPresent());
        verify(brokerRepository, times(1)).getFirstUnprocessedMessage();
    }

}