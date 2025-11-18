package org.vl4ds4m.banking.converter.client;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.vl4ds4m.banking.common.properties.RatesClientProperties;
import org.vl4ds4m.banking.rates.openapi.client.invoke.ApiClient;

@Configuration
@EnableConfigurationProperties(RatesClientProperties.class)
@RequiredArgsConstructor
public class RatesClientConfig {

    private final RatesClientProperties ratesProps;

    @Bean
    public RatesClient ratesClient(RestTemplate restTemplate) {
        var client = new ApiClient(restTemplate);
        client.setBasePath(ratesProps.httpUrl());
        return new RatesClient(client);
    }
}
