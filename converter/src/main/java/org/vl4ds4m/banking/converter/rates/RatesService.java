package org.vl4ds4m.banking.converter.rates;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.vl4ds4m.banking.converter.client.rates.RatesApi;
import org.vl4ds4m.banking.converter.client.rates.model.RatesResponse;

import java.math.BigDecimal;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class RatesService {

    private final RatesApi ratesClient;

    private final RetryTemplate retryTemplate;

    private final ObservationRegistry observationRegistry;

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
        log.info("Request currency rates");
        try {
            return ratesClient.getRates();
        } catch (RestClientException e) {
            throw new RatesServiceException("Service is unavailable: " + e.getMessage());
        }
    }

    private static void checkRatesResponse(RatesResponse response) {
        if (response == null) {
            throw new RatesServiceException("RatesResponse is null");
        }
        var base = response.getBase();
        if (base == null) {
            throw new RatesServiceException("RatesResponse base is null");
        }
        Map<String, BigDecimal> rates = response.getRates();
        if (rates == null) {
            throw new RatesServiceException("RatesResponse rates is null");
        }
    }
}
