package edu.vl4ds4m.tbank.converter.config;

import edu.vl4ds4m.tbank.converter.auth.AuthInterceptor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
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
                .connectTimeout(Duration.ofSeconds(timeout))
                .readTimeout(Duration.ofSeconds(timeout))
                .build();
    }

    @Bean
    @Profile("auth")
    @Qualifier("auth")
    public RestTemplate restTemplateWithAuth(
            RestTemplateBuilder restTemplateBuilder,
            AuthInterceptor authInterceptor
    ) {
        return restTemplateBuilder
                .connectTimeout(Duration.ofSeconds(timeout))
                .readTimeout(Duration.ofSeconds(timeout))
                .additionalInterceptors(authInterceptor)
                .build();
    }
}
