package dev.filiptanu.h4task.messagebroker.broker;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@WebMvcTest(ProducerController.class)
public class ProducerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void receiveProducerMessage_shouldReturnOk() throws Exception {
        mockMvc.perform(post("/receiveProducerMessage")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"body\": \"Some message...\"}"))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void receiveProducerMessage_shouldReturnClientError() throws Exception {
        mockMvc.perform(post("/receiveProducerMessage")
                .contentType(MediaType.APPLICATION_JSON)
                .content("Some message..."))
                .andExpect(status().is4xxClientError());
    }

}