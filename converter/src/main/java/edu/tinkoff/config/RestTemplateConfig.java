package edu.tinkoff.config;

import edu.tinkoff.auth.AuthInterceptor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class RestTemplateConfig {
    private long timeout;

    @Value("${services.timeout:0}")
    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    @Bean
    @Primary
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(timeout))
                .setReadTimeout(Duration.ofSeconds(timeout))
                .build();
    }

    @Bean
    @Qualifier("auth")
    public RestTemplate authRestTemplate(
            RestTemplateBuilder restTemplateBuilder,
            AuthInterceptor authInterceptor
    ) {
        return restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(timeout))
                .setReadTimeout(Duration.ofSeconds(timeout))
                .additionalInterceptors(authInterceptor)
                .build();
    }
}
