package edu.tinkoff.service;

import edu.tinkoff.dto.RatesResposne;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RatesService {
    private final RestTemplate restTemplate;
    private final RetryTemplate retryTemplate;

    private String currencyRatesUrl;

    public RatesService(
            @Qualifier("auth") RestTemplate restTemplate,
            RetryTemplate retryTemplate
    ) {
        this.restTemplate = restTemplate;
        this.retryTemplate = retryTemplate;
    }

    @Value("${services.currency-rates.url}")
    public void setCurrencyRatesUrl(String currencyRatesUrl) {
        this.currencyRatesUrl = currencyRatesUrl;
    }

    public RatesResposne getRatesResponse() {
        RetryCallback<RatesResposne, RuntimeException> retryCallback =
                context -> restTemplate.getForObject(currencyRatesUrl, RatesResposne.class);
        return retryTemplate.execute(retryCallback);
    }
}
