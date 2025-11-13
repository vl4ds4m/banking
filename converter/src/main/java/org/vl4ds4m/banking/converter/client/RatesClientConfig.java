package org.vl4ds4m.banking.converter.client;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;
import org.vl4ds4m.banking.common.auth.Auth;
import org.vl4ds4m.banking.common.properties.RatesProperties;
import org.vl4ds4m.banking.rates.client.http.invoker.ApiClient;

@Configuration
@EnableConfigurationProperties(RatesProperties.class)
@RequiredArgsConstructor
public class RatesClientConfig {

    private final RatesProperties ratesProps;

    @Bean
    @Profile("!" + Auth.PROFILE)
    public RatesClient ratesClient(RestTemplate restTemplate) {
        return createRatesClient(restTemplate);
    }

    @Bean
    @Profile(Auth.PROFILE)
    public RatesClient ratesClientWithAuth(@Auth RestTemplate restTemplate) {
        return createRatesClient(restTemplate);
    }

    private RatesClient createRatesClient(RestTemplate restTemplate) {
        var client = new ApiClient(restTemplate);
        client.setBasePath(ratesProps.httpUrl());
        return new RatesClient(client);
    }
}
