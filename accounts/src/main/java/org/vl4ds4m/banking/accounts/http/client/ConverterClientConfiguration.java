package org.vl4ds4m.banking.accounts.http.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.vl4ds4m.banking.converter.http.client.ConvertApi;
import org.vl4ds4m.banking.converter.http.client.invoker.ApiClient;

@Configuration
public class ConverterClientConfiguration {

    @Bean
    public ConverterClient converterClient(
            @Value("${services.converter.address}") String address,
            RestTemplate restTemplate
    ) {
        var client = new ApiClient(restTemplate);
        client.setBasePath("http://" + address);
        var api = new ConvertApi(client);
        return new ConverterClient(api);
    }
}
