package dev.filiptanu.h4task.messagebroker.broker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.ConnectException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import dev.filiptanu.h4task.messagebroker.core.ConsumerMessage;
import dev.filiptanu.h4task.messagebroker.core.SubscribeConsumerMessage;

@RunWith(MockitoJUnitRunner.class)
public class BrokerServiceTest {

    @InjectMocks
    @Spy
    private BrokerService brokerService;

    @Mock
    private BrokerRepository brokerRepository;

    @Mock
    private List<SubscribeConsumerMessage> consumers;

    @Mock
    private RestTemplate restTemplate;

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
        brokerService.confirmMessage(1, "1");

        verify(brokerRepository, times(1)).confirmMessage(1, "1");
    }

    @Test
    public void clearPendingMessages() {
        brokerService.clearPendingMessages();

        verify(brokerRepository, times(1)).clearPendingMessages();
    }

    @Test
    public void addConsumer() {
        SubscribeConsumerMessage subscribeConsumerMessage = new SubscribeConsumerMessage();
        subscribeConsumerMessage.setConsumerId("1");
        subscribeConsumerMessage.setHealthcheckEndpoint("http://localhost:8081/healthcheck");
        subscribeConsumerMessage.setHealthcheckEndpoint("http://localhost:8081/pushConsumerMessage");

        brokerService.addConsumer(subscribeConsumerMessage);

        verify(consumers, times(1)).add(subscribeConsumerMessage);
    }

    @Test
    public void pushMessageToConsumer_consumerPresent_repositoryReturnsEntity_shouldPushMessage() {
        SubscribeConsumerMessage subscribeConsumerMessage = new SubscribeConsumerMessage();
        subscribeConsumerMessage.setConsumerId("1");
        subscribeConsumerMessage.setHealthcheckEndpoint("http://localhost:8081/healthcheck");
        subscribeConsumerMessage.setHealthcheckEndpoint("http://localhost:8081/pushConsumerMessage");

        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setId(1);
        messageEntity.setBody("Some message...");
        messageEntity.setReceivedFromProducer(LocalDateTime.now());
        messageEntity.setProcessingStatus(ProcessingStatus.NOT_PROCESSED);
        messageEntity.setConsumerId("1");

        when(consumers.size()).thenReturn(1);
        when(consumers.get(anyInt())).thenReturn(subscribeConsumerMessage);
        when(brokerRepository.getFirstUnprocessedMessage("1")).thenReturn(messageEntity);

        brokerService.pushMessageToConsumer();

        verify(consumers, times(2)).size();
        verify(consumers, times(1)).get(anyInt());
        verify(brokerRepository, times(1)).getFirstUnprocessedMessage(anyString());
        verify(restTemplate, times(1)).postForEntity(subscribeConsumerMessage.getPushEndpoint(), messageEntity.toConsumerMessage(), Void.class);
    }

    @Test(expected = NoRegisteredConsumersException.class)
    public void pushMessageToConsumer_consumerNotPresent_shouldThrowNoRegisteredConsumersException() {
        when(consumers.size()).thenReturn(0);

        brokerService.pushMessageToConsumer();
    }

    @Test(expected = NoMessagesPresentAtBrokerException.class)
    public void pushMessageToConsumer_consumerPresent_repositoryThrowsException_shouldThrowNoMessagesPresentAtBrokerException() {
        SubscribeConsumerMessage subscribeConsumerMessage = new SubscribeConsumerMessage();
        subscribeConsumerMessage.setConsumerId("1");
        subscribeConsumerMessage.setHealthcheckEndpoint("http://localhost:8081/healthcheck");
        subscribeConsumerMessage.setHealthcheckEndpoint("http://localhost:8081/pushConsumerMessage");

        when(consumers.size()).thenReturn(1);
        when(consumers.get(anyInt())).thenReturn(subscribeConsumerMessage);
        when(brokerRepository.getFirstUnprocessedMessage("1")).thenThrow(EmptyResultDataAccessException.class);

        brokerService.pushMessageToConsumer();
    }

    @Test
    public void pushMessageToConsumer_consumerConnectionRefused_shoudRemoveConsumer() {
        SubscribeConsumerMessage subscribeConsumerMessage = new SubscribeConsumerMessage();
        subscribeConsumerMessage.setConsumerId("1");
        subscribeConsumerMessage.setHealthcheckEndpoint("http://localhost:8081/healthcheck");
        subscribeConsumerMessage.setHealthcheckEndpoint("http://localhost:8081/pushConsumerMessage");

        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setId(1);
        messageEntity.setBody("Some message...");
        messageEntity.setReceivedFromProducer(LocalDateTime.now());
        messageEntity.setProcessingStatus(ProcessingStatus.NOT_PROCESSED);
        messageEntity.setConsumerId("1");

        when(consumers.size()).thenReturn(1);
        when(consumers.get(anyInt())).thenReturn(subscribeConsumerMessage);
        when(brokerRepository.getFirstUnprocessedMessage("1")).thenReturn(messageEntity);
        when(restTemplate.postForEntity(subscribeConsumerMessage.getPushEndpoint(), messageEntity.toConsumerMessage(), Void.class)).thenThrow(RestClientException.class);

        brokerService.pushMessageToConsumer();

        verify(consumers, times(2)).size();
        verify(consumers, times(1)).get(anyInt());
        verify(brokerRepository, times(1)).getFirstUnprocessedMessage(anyString());
        verify(restTemplate, times(1)).postForEntity(subscribeConsumerMessage.getPushEndpoint(), messageEntity.toConsumerMessage(), Void.class);
        verify(consumers, times(1)).remove(subscribeConsumerMessage);
    }

}