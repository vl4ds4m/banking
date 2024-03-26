package edu.tinkoff.service;

import edu.tinkoff.dto.RatesResposne;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RatesService {
    private final RestTemplate restTemplate;
    private String currencyRatesUrl;

    public RatesService(@Qualifier("auth") RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Value("${services.currency-rates.url}")
    public void setCurrencyRatesUrl(String currencyRatesUrl) {
        this.currencyRatesUrl = currencyRatesUrl;
    }

    public RatesResposne getRatesResponse() {
        return restTemplate.getForObject(currencyRatesUrl, RatesResposne.class);
    }
}
