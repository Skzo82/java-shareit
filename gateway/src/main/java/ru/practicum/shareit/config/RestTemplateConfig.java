package ru.practicum.shareit.config;

import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.util.Timeout;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.Duration;

@Configuration
public class RestTemplateConfig {

    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(5);
    private static final Duration READ_TIMEOUT = Duration.ofSeconds(30);

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .requestFactory(settings -> {
                    RequestConfig requestConfig = RequestConfig.custom()
                            .setConnectTimeout(Timeout.ofMilliseconds(CONNECT_TIMEOUT.toMillis()))
                            .setResponseTimeout(Timeout.ofMilliseconds(READ_TIMEOUT.toMillis()))
                            .build();

                    CloseableHttpClient httpClient = HttpClients.custom()
                            .setDefaultRequestConfig(requestConfig)
                            .evictExpiredConnections()
                            .build();

                    return new HttpComponentsClientHttpRequestFactory(httpClient);
                })
                .errorHandler(new DefaultResponseErrorHandler() {
                    @Override
                    public boolean hasError(ClientHttpResponse response) throws IOException {
                        return false;
                    }
                })
                .build();
    }
}