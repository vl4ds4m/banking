package org.vl4ds4m.banking.common.auth;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Auth
@EnableConfigurationProperties(KeycloakProperties.class)
public class AuthConfig {

    @Bean
    public KeycloakAuthClient keycloakAuthClient(
            RestTemplateBuilder restTemplateBuilder,
            KeycloakProperties properties
    ) {
        return new KeycloakAuthClient(restTemplateBuilder.build(), properties);
    }

    @Bean
    public AuthInterceptor authInterceptor(KeycloakAuthClient keycloakAuthClient) {
        return new AuthInterceptor(keycloakAuthClient);
    }
}
