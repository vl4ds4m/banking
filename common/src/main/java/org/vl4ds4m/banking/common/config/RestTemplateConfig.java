package org.vl4ds4m.banking.common.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.boot.restclient.autoconfigure.RestTemplateBuilderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;
import org.vl4ds4m.banking.common.auth.AuthInterceptor;
import org.vl4ds4m.banking.common.handler.log.HttpClientLogInterceptor;

import java.util.ArrayList;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplateBuilder restTemplateBuilder(
            RestTemplateBuilderConfigurer configurer,
            HttpClientLogInterceptor httpClientLog
    ) {
        return configurer.configure(new RestTemplateBuilder())
                .interceptors(httpClientLog);
    }

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
