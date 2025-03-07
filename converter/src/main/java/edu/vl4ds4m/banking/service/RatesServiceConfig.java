package edu.vl4ds4m.banking.service;

import io.micrometer.observation.ObservationRegistry;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RatesServiceConfig {
    private final String currencyRatesHost;
    private final RetryTemplate retryTemplate;
    private final ObservationRegistry observationRegistry;

    public RatesServiceConfig(
            @Value("${services.currency-rates.host}")
            String currencyRatesHost,
            RetryTemplate retryTemplate,
            ObservationRegistry observationRegistry
    ) {
        this.currencyRatesHost = currencyRatesHost;
        this.retryTemplate = retryTemplate;
        this.observationRegistry = observationRegistry;
    }

    @Bean
    @Profile("!auth")
    public RatesService ratesService(RestTemplate restTemplate) {
        return new RatesService(currencyRatesHost, restTemplate, retryTemplate, observationRegistry);
    }

    @Bean
    @Profile("auth")
    public RatesService ratesServiceWithAuth(@Qualifier("auth") RestTemplate restTemplate) {
        return new RatesService(currencyRatesHost, restTemplate, retryTemplate, observationRegistry);
    }
}
