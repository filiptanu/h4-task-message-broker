package dev.filiptanu.h4task.messagebroker.broker;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@WebMvcTest(ProducerController.class)
public class ProducerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BrokerService brokerService;

    @Test
    public void receiveProducerMessage_validMessage_shouldReturnOk() throws Exception {
        mockMvc.perform(post("/receiveProducerMessage")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"body\": \"Some message...\"}"))
                .andExpect(status().is2xxSuccessful());

        verify(brokerService, times(1)).processReceivedMessage("Some message...");
    }

    @Test
    public void receiveProducerMessage_invalidMessageNotJson_shouldReturnClientError() throws Exception {
        mockMvc.perform(post("/receiveProducerMessage")
                .contentType(MediaType.APPLICATION_JSON)
                .content("Some message..."))
                .andExpect(status().is4xxClientError());

        verify(brokerService, never()).processReceivedMessage(anyString());
    }

    @Test
    public void receiveProducerMessage_invalidMessageNullBody_shouldReturnClientError() throws Exception {
        mockMvc.perform(post("/receiveProducerMessage")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"body\": null}"))
                .andExpect(status().is4xxClientError());

        verify(brokerService, never()).processReceivedMessage(anyString());
    }

}