package org.vl4ds4m.banking.common.config;

import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.boot.restclient.autoconfigure.RestTemplateBuilderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.vl4ds4m.banking.common.handler.log.HttpClientLogInterceptor;

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

}
