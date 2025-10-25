package org.vl4ds4m.banking.accounts.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.vl4ds4m.banking.accounts.client.converter.ConvertApi;
import org.vl4ds4m.banking.accounts.client.converter.invoker.ApiClient;

@Configuration
public class ConverterClientConfiguration {

    @Bean
    public ConverterClient converterClient(
            @Value("${converter.url}") String url,
            RestTemplate restTemplate
    ) {
        var client = new ApiClient(restTemplate);
        client.setBasePath(url);
        var api = new ConvertApi(client);
        return new ConverterClient(api);
    }
}
