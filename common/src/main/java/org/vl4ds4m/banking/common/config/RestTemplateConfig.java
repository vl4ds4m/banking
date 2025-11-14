package org.vl4ds4m.banking.common.config;

import org.springframework.boot.autoconfigure.web.client.RestTemplateBuilderConfigurer;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;
import org.vl4ds4m.banking.common.auth.Auth;
import org.vl4ds4m.banking.common.auth.AuthInterceptor;

@Configuration
public class RestTemplateConfig {

    @Bean
    @Primary
    public RestTemplate simpleRestTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder.build();
    }

    @Bean
    @Auth
    @Profile(Auth.PROFILE)
    public RestTemplate restTemplateWithAuth(
            RestTemplateBuilder restTemplateBuilder,
            AuthInterceptor authInterceptor
    ) {
        return restTemplateBuilder
                .additionalInterceptors(authInterceptor)
                .build();
    }
}
