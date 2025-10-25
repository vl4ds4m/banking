package org.vl4ds4m.banking.converter.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;
import org.vl4ds4m.banking.common.auth.Auth;
import org.vl4ds4m.banking.converter.client.rates.RatesApi;
import org.vl4ds4m.banking.converter.client.rates.invoker.ApiClient;

@Configuration
public class RatesClientConfiguration {

    @Value("${services.currency-rates-host}")
    private String url;

    @Bean
    @Profile("!" + Auth.PROFILE)
    public RatesClient ratesClient(RestTemplate restTemplate) {
        return createRatesClient(restTemplate);
    }

    @Bean
    @Profile(Auth.PROFILE)
    public RatesClient ratesClientWithAuth(@Auth RestTemplate restTemplate) {
        return createRatesClient(restTemplate);
    }

    private RatesClient createRatesClient(RestTemplate restTemplate) {
        var client = new ApiClient(restTemplate);
        client.setBasePath(url);
        var api = new RatesApi(client);
        return new RatesClient(api);
    }
}
