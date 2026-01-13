package org.vl4ds4m.banking.accounts.client.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.client.OAuth2ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;
import org.vl4ds4m.banking.accounts.App;
import org.vl4ds4m.banking.accounts.client.ConverterClientImpl;
import org.vl4ds4m.banking.common.properties.ConverterClientProperties;
import org.vl4ds4m.banking.converter.openapi.client.invoke.ApiClient;
import org.vl4ds4m.banking.converter.openapi.server.api.ConvertApi;

@Configuration
@ConditionalOnBooleanProperty(
        name = ConverterClientProperties.GRPC_PROP,
        havingValue = false)
@EnableConfigurationProperties(ConverterClientProperties.class)
@Slf4j
@RequiredArgsConstructor
public class ConverterRestClientConfig {

    private final ConverterClientProperties converterProps;

    @Bean
    public ConverterClientImpl converterHttpClient(RestTemplate restTemplate) {
        log.info("Create HTTP converter client");
        var client = new ApiClient(restTemplate);
        client.setBasePath(converterProps.httpUrl());
        return new ConverterRestClient(client);
    }

    @Bean
    public RestTemplate restTemplate(
            RestTemplateBuilder builder,
            OAuth2AuthorizedClientManager authorizedClientManager
    ) {
        OAuth2ClientHttpRequestInterceptor oauth2Interceptor = createOAuth2Interceptor(authorizedClientManager);
        return builder.additionalInterceptors(oauth2Interceptor).build();
    }

    private static OAuth2ClientHttpRequestInterceptor createOAuth2Interceptor(
            OAuth2AuthorizedClientManager authorizedClientManager
    ) {
        var interceptor = new OAuth2ClientHttpRequestInterceptor(authorizedClientManager);
        interceptor.setClientRegistrationIdResolver(request -> {
            String path = request.getURI().getPath();
            return path.equals(ConvertApi.PATH_CONVERT_CURRENCY)
                    ? App.OAUTH2_CLIENT_REG
                    : null;
        });
        return interceptor;
    }

}
