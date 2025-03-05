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
    private final String currencyRatesUrl;
    private final RetryTemplate retryTemplate;
    private final ObservationRegistry observationRegistry;

    public RatesServiceConfig(
            @Value("${services.currency-rates.url}")
            String currencyRatesUrl,
            RetryTemplate retryTemplate,
            ObservationRegistry observationRegistry
    ) {
        this.currencyRatesUrl = currencyRatesUrl;
        this.retryTemplate = retryTemplate;
        this.observationRegistry = observationRegistry;
    }

    @Bean
    @Profile("!auth")
    public RatesService ratesService(RestTemplate restTemplate) {
        return new RatesService(currencyRatesUrl, restTemplate, retryTemplate, observationRegistry);
    }

    @Bean
    @Profile("auth")
    public RatesService ratesServiceWithAuth(@Qualifier("auth") RestTemplate restTemplate) {
        return new RatesService(currencyRatesUrl, restTemplate, retryTemplate, observationRegistry);
    }
}
