package org.vl4ds4m.banking.accounts.client.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.vl4ds4m.banking.accounts.client.ConverterClient;
import org.vl4ds4m.banking.common.properties.ConverterClientProperties;
import org.vl4ds4m.banking.converter.openapi.client.invoke.ApiClient;

@Configuration
@ConditionalOnProperty(
        name = ConverterClientProperties.GRPC_PROP,
        havingValue = "false")
@EnableConfigurationProperties(ConverterClientProperties.class)
@Slf4j
@RequiredArgsConstructor
public class ConverterRestClientConfig {

    private final ConverterClientProperties converterProps;

    @Bean
    public ConverterClient converterHttpClient(RestTemplate restTemplate) {
        log.info("Create HTTP converter client");
        var client = new ApiClient(restTemplate);
        client.setBasePath(converterProps.httpUrl());
        return new ConverterRestClient(client);
    }
}
