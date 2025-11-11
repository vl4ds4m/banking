package org.vl4ds4m.banking.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;
import org.vl4ds4m.banking.common.auth.Auth;
import org.vl4ds4m.banking.common.auth.AuthInterceptor;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Configuration
public class RestTemplateConfig {
    private final Duration timeout;

    public RestTemplateConfig(
        @Value("${services.timeout:0}")
        @DurationUnit(ChronoUnit.SECONDS)
        Duration timeout
    ) {
        this.timeout = timeout;
    }

    @Bean
    @Primary
    public RestTemplate simpleRestTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder
            .connectTimeout(timeout)
            .readTimeout(timeout)
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
            .connectTimeout(timeout)
            .readTimeout(timeout)
            .additionalInterceptors(authInterceptor)
            .build();
    }
}
