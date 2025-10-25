package org.vl4ds4m.banking.converter.client;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.RestClientException;
import org.vl4ds4m.banking.converter.client.rates.RatesApi;
import org.vl4ds4m.banking.converter.client.rates.model.RatesResponse;
import org.vl4ds4m.banking.converter.service.exception.RatesServiceException;

@Slf4j
@RequiredArgsConstructor
public class RatesClient {

    private final RatesApi api;

    private final RetryTemplate retryTemplate;

    private final ObservationRegistry observationRegistry;

    public RatesResponse getRates() {
        RetryCallback<RatesResponse, RatesServiceException> retryCallback =
                context -> Observation
                        .createNotStarted("rates", observationRegistry)
                        .observe(this::requestRates);
        return retryTemplate.execute(retryCallback);
    }

    private RatesResponse requestRates() {
        log.info("Request currency rates");
        try {
            return api.getRates();
        } catch (RestClientException e) {
            throw new RatesServiceException("Service is unavailable: " + e.getMessage());
        }
    }
}
