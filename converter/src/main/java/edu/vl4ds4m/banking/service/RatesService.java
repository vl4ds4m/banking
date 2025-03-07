package edu.vl4ds4m.banking.service;

import edu.vl4ds4m.banking.exception.RatesServiceException;
import edu.vl4ds4m.banking.dto.Currency;
import edu.vl4ds4m.banking.dto.RatesResponse;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;

public class RatesService {
    private static final String PATH = "/rates";

    private static final Logger logger = LoggerFactory.getLogger(RatesService.class);

    private final RestTemplate restTemplate;
    private final RetryTemplate retryTemplate;
    private final ObservationRegistry observationRegistry;

    private final String currencyRatesUrl;

    public RatesService(
            String currencyRatesHost,
            RestTemplate restTemplate,
            RetryTemplate retryTemplate,
            ObservationRegistry observationRegistry
    ) {
        this.currencyRatesUrl = currencyRatesHost + PATH;
        this.restTemplate = restTemplate;
        this.retryTemplate = retryTemplate;
        this.observationRegistry = observationRegistry;
    }

    public RatesResponse getRatesResponse() {
        RetryCallback<RatesResponse, RatesServiceException> retryCallback =
                context -> Observation
                        .createNotStarted("rates", observationRegistry)
                        .observe(this::requestRatesResponse);
        RatesResponse response = retryTemplate.execute(retryCallback);
        checkRatesResponse(response);
        return response;
    }

    private RatesResponse requestRatesResponse() {
        logger.debug("Send a request to get currency rates");
        try {
            return restTemplate.getForObject(currencyRatesUrl, RatesResponse.class);
        } catch (RestClientException e) {
            throw new RatesServiceException("Service is unavailable: " + e.getMessage());
        }
    }

    private static void checkRatesResponse(RatesResponse response) {
        if (response == null) {
            throw new RatesServiceException("RatesResponse is null");
        }
        Currency base = response.getBase();
        if (base == null) {
            throw new RatesServiceException("RatesResponse base is null");
        }
        Map<String, BigDecimal> rates = response.getRates();
        if (rates == null) {
            throw new RatesServiceException("RatesResponse rates is null");
        }
    }
}
