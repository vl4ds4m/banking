package org.vl4ds4m.banking.accounts.client.converter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConverterClientConfiguration {

    @Bean
    public ConvertApi converterClient(@Value("${converter.url}") String url) {
        var client = new ConvertApi();
        client.getApiClient().setBasePath(url);
        return client;
    }
}
