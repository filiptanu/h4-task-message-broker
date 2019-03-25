package dev.filiptanu.h4task.messagebroker.broker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import dev.filiptanu.h4task.messagebroker.core.SubscribeConsumerMessage;

@Configuration
public class Config {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    // TODO (filip): Maybe use Map instead, for easy removal of consumers
    @Bean
    public List<SubscribeConsumerMessage> getConsumers() {
        return Collections.synchronizedList(new ArrayList<>());
    }

}