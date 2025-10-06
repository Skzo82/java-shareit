package ru.practicum.shareit.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class RestTemplateConfig {

    // Создаём бин RestTemplate с базовым адресом на сервер
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder,
                                     @Value("${shareit-server.url}") String serverUrl) {
        return builder
                .rootUri(serverUrl)                 // http://localhost:9090
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(30))
                .build();
    }
}
