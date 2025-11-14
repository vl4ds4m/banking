package org.vl4ds4m.banking.common.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;
import org.vl4ds4m.banking.common.auth.AuthInterceptor;

import java.util.ArrayList;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate mainRestTemplate(
            RestTemplateBuilder builder,

            @Autowired(required = false)
            AuthInterceptor authInterceptor
    ) {
        var interceptors = new ArrayList<ClientHttpRequestInterceptor>();

        if (authInterceptor != null) {
            interceptors.add(authInterceptor);
        }

        return builder.additionalInterceptors(interceptors).build();
    }
}
