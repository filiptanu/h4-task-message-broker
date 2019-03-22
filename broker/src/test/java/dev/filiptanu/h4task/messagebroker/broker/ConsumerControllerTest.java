package dev.filiptanu.h4task.messagebroker.broker;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import dev.filiptanu.h4task.messagebroker.core.ConsumerMessage;

@RunWith(SpringRunner.class)
@WebMvcTest(ConsumerController.class)
public class ConsumerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BrokerService brokerService;

    @Test
    public void receiveProducerMessage_consumerIdPresent_shouldReturnMessage() throws Exception {
        ConsumerMessage consumerMessage = new ConsumerMessage();
        consumerMessage.setMessageId(1);
        consumerMessage.setBody("Some message...");

        when(brokerService.consumeMessage("1")).thenReturn(Optional.of(consumerMessage));

        mockMvc.perform(get("/sendConsumerMessage")
                .param("consumerId", "1"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string(containsString("Some message...")));

        verify(brokerService, times(1)).consumeMessage("1");
    }

    @Test
    public void receiveProducerMessage_consumerIdNotPresent_shouldReturnClientError() throws Exception {
        ConsumerMessage consumerMessage = new ConsumerMessage();
        consumerMessage.setMessageId(1);
        consumerMessage.setBody("Some message...");

        when(brokerService.consumeMessage("1")).thenReturn(Optional.of(consumerMessage));

        mockMvc.perform(get("/sendConsumerMessage"))
                .andExpect(status().is4xxClientError());

        verify(brokerService, never()).processReceivedMessage(anyString());
    }

    @Test
    public void receiveProducerMessage_consumerIdPresent_shouldReturnNotFound() throws Exception {
        when(brokerService.consumeMessage("1")).thenReturn(Optional.empty());

        mockMvc.perform(get("/sendConsumerMessage")
                .param("consumerId", "1"))
                .andExpect(status().isNotFound());

        verify(brokerService, times(1)).consumeMessage("1");
    }

    @Test
    public void confirmMessage_validConfirmMessage_shouldReturnOk() throws Exception {
        mockMvc.perform(post("/confirmMessage")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"messageId\": 1, \"consumerId\": \"1\"}"))
                .andExpect(status().is2xxSuccessful());

        verify(brokerService, times(1)).confirmMessage(1, "1");
    }

    @Test
    public void confirmMessage_invalidConfirmMessageNotJson_shouldReturnClientError() throws Exception {
        mockMvc.perform(post("/confirmMessage")
                .contentType(MediaType.APPLICATION_JSON)
                .content("Some message..."))
                .andExpect(status().is4xxClientError());

        verify(brokerService, never()).confirmMessage(anyInt(), anyString());
    }

    @Test
    public void confirmMessage_invalidProducerMessageNullConsumerId_shouldReturnClientError() throws Exception {
        mockMvc.perform(post("/confirmMessage")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"messageId\": 1, \"consumerId\": null}"))
                .andExpect(status().is4xxClientError());

        verify(brokerService, never()).confirmMessage(anyInt(), anyString());
    }

}