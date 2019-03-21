package dev.filiptanu.h4task.messagebroker.broker;

import static org.hamcrest.Matchers.containsString;
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
import dev.filiptanu.h4task.messagebroker.core.Message;

@RunWith(SpringRunner.class)
@WebMvcTest(ConsumerController.class)
public class ConsumerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BrokerService brokerService;

    @Test
    public void receiveProducerMessage_idPresent_shouldReturnMessage() throws Exception {
        Message message = new Message();
        message.setBody("Some message...");

        when(brokerService.consumeMessage()).thenReturn(Optional.of(message));

        mockMvc.perform(get("/sendConsumerMessage")
                .param("id", "1"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string(containsString("Some message...")));

        verify(brokerService, times(1)).consumeMessage();
    }

    @Test
    public void receiveProducerMessage_idNotPresent_shouldReturnClientError() throws Exception {
        Message message = new Message();
        message.setBody("Some message...");

        when(brokerService.consumeMessage()).thenReturn(Optional.of(message));

        mockMvc.perform(get("/sendConsumerMessage"))
                .andExpect(status().is4xxClientError());

        verify(brokerService, never()).processReceivedMessage(anyString());
    }

    @Test
    public void receiveProducerMessage_idPresent_shouldReturnNotFound() throws Exception {
        when(brokerService.consumeMessage()).thenReturn(Optional.empty());

        mockMvc.perform(get("/sendConsumerMessage")
                .param("id", "1"))
                .andExpect(status().isNotFound());

        verify(brokerService, times(1)).consumeMessage();
    }

}