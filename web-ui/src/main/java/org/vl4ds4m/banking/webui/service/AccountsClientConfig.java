package org.vl4ds4m.banking.webui.service;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.vl4ds4m.banking.accounts.openapi.client.api.AccountsApi;
import org.vl4ds4m.banking.accounts.openapi.client.api.CustomersApi;
import org.vl4ds4m.banking.accounts.openapi.client.api.TransferApi;
import org.vl4ds4m.banking.accounts.openapi.client.invoke.ApiClient;
import org.vl4ds4m.banking.common.properties.AccountsClientProperties;

@Configuration
@EnableConfigurationProperties(AccountsClientProperties.class)
@RequiredArgsConstructor
public class AccountsClientConfig {

    private final AccountsClientProperties properties;

    private final RestTemplate restTemplate;

    @Bean
    public ApiClient accountsApiClient() {
        var client = new ApiClient(restTemplate);
        client.setBasePath(properties.httpUrl());
        return client;
    }

    @Bean
    public CustomersApi customerClient() {
        return new CustomersApi(accountsApiClient());
    }

    @Bean
    public AccountsApi accountClient() {
        return new AccountsApi(accountsApiClient());
    }

    @Bean
    public TransferApi transferApi() {
        return new TransferApi(accountsApiClient());
    }
}
