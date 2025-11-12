package org.vl4ds4m.banking.converter.http.client;

import io.micrometer.observation.ObservationRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.RestTemplate;
import org.vl4ds4m.banking.common.auth.Auth;
import org.vl4ds4m.banking.rates.http.client.RatesApi;
import org.vl4ds4m.banking.rates.http.client.invoker.ApiClient;

@Configuration
public class RatesClientConfiguration {

    @Value("${services.currency-rates-host}")
    private String url;

    @Bean
    @Profile("!" + Auth.PROFILE)
    public RatesClient ratesClient(
            RestTemplate restTemplate,
            RetryTemplate retryTemplate,
            ObservationRegistry observationRegistry
    ) {
        return createRatesClient(restTemplate, retryTemplate, observationRegistry);
    }

    @Bean
    @Profile(Auth.PROFILE)
    public RatesClient ratesClientWithAuth(
            @Auth RestTemplate restTemplate,
            RetryTemplate retryTemplate,
            ObservationRegistry observationRegistry
    ) {
        return createRatesClient(restTemplate, retryTemplate, observationRegistry);
    }

    private RatesClient createRatesClient(
            RestTemplate restTemplate,
            RetryTemplate retryTemplate,
            ObservationRegistry observationRegistry
    ) {
        var client = new ApiClient(restTemplate);
        client.setBasePath(url);
        var api = new RatesApi(client);
        return new RatesClient(api, retryTemplate, observationRegistry);
    }
}
