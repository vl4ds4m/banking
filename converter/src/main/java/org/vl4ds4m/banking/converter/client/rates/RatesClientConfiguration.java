package org.vl4ds4m.banking.converter.client.rates;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;
import org.vl4ds4m.banking.auth.Auth;
import org.vl4ds4m.banking.converter.client.rates.invoker.ApiClient;

@Configuration
public class RatesClientConfiguration {

    @Value("${services.currency-rates-host}")
    private String url;

    @Bean
    @Profile("!" + Auth.PROFILE)
    public RatesApi ratesClient(RestTemplate restTemplate) {
        return createRatesClient(restTemplate);
    }

    @Bean
    @Profile(Auth.PROFILE)
    public RatesApi ratesClientWithAuth(@Auth RestTemplate restTemplate) {
        return createRatesClient(restTemplate);
    }

    private RatesApi createRatesClient(RestTemplate restTemplate) {
        var client = new ApiClient(restTemplate);
        client.setBasePath(url);
        return new RatesApi(client);
    }
}
