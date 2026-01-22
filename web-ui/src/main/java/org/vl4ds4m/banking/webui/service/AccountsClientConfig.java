package org.vl4ds4m.banking.webui.service;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.web.client.OAuth2ClientHttpRequestInterceptor;
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

    @Bean
    public RestTemplate restTemplate(
            RestTemplateBuilder builder,
            OAuth2AuthorizedClientManager authorizedClientManager
    ) {
        var oauth2Interceptor = createOauth2Interceptor(authorizedClientManager);
        return builder.additionalInterceptors(oauth2Interceptor).build();
    }

    @Bean
    public ApiClient accountsApiClient(RestTemplate restTemplate) {
        var client = new ApiClient(restTemplate);
        client.setBasePath(properties.httpUrl());
        return client;
    }

    @Bean
    public CustomersApi customerClient(ApiClient apiClient) {
        return new CustomersApi(apiClient);
    }

    @Bean
    public AccountsApi accountClient(ApiClient apiClient) {
        return new AccountsApi(apiClient);
    }

    @Bean
    public TransferApi transferApi(ApiClient apiClient) {
        return new TransferApi(apiClient);
    }

    private static OAuth2ClientHttpRequestInterceptor createOauth2Interceptor(
            OAuth2AuthorizedClientManager authorizedClientManager
    ) {
        var interceptor = new OAuth2ClientHttpRequestInterceptor(authorizedClientManager);
        interceptor.setClientRegistrationIdResolver(_ -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            return (authentication instanceof OAuth2AuthenticationToken principal)
                    ? principal.getAuthorizedClientRegistrationId()
                    : null;
        });
        return interceptor;
    }

}
