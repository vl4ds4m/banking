package edu.vl4ds4m.banking.rates;

import edu.vl4ds4m.banking.auth.Auth;
import edu.vl4ds4m.banking.auth.AuthInterceptor;
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
    private final long timeout;

    public RestTemplateConfig(@Value("${services.timeout:0}") long timeout) {
        this.timeout = timeout;
    }

    @Bean
    @Primary
    public RestTemplate simpleRestTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder
            .connectTimeout(Duration.ofSeconds(timeout))
            .readTimeout(Duration.ofSeconds(timeout))
            .build();
    }

    @Bean
    @Auth
    @Profile(Auth.PROFILE)
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
