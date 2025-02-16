package edu.vl4ds4m.tbank.service;

import edu.vl4ds4m.tbank.dto.RatesResposne;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RatesService {
    private static final Logger logger = LoggerFactory.getLogger(RatesService.class);

    private final RestTemplate restTemplate;
    private final RetryTemplate retryTemplate;
    private final ObservationRegistry observationRegistry;

    private String currencyRatesUrl;

    public RatesService(
            @Qualifier("auth") RestTemplate restTemplate,
            RetryTemplate retryTemplate,
            ObservationRegistry observationRegistry
    ) {
        this.restTemplate = restTemplate;
        this.retryTemplate = retryTemplate;
        this.observationRegistry = observationRegistry;
    }

    @Value("${services.currency-rates.url}")
    public void setCurrencyRatesUrl(String currencyRatesUrl) {
        this.currencyRatesUrl = currencyRatesUrl;
    }

    public RatesResposne getRatesResponse() {
        RetryCallback<RatesResposne, RuntimeException> retryCallback = context ->
                Observation.createNotStarted("rates", observationRegistry)
                        .observe(() -> {
                            logger.info("Send a request to get currency rates");
                            return restTemplate.getForObject(currencyRatesUrl, RatesResposne.class);
                        });
        return retryTemplate.execute(retryCallback);
    }
}
