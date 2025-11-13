package org.vl4ds4m.banking.converter.client;

import lombok.extern.slf4j.Slf4j;
import org.vl4ds4m.banking.common.entity.Currency;
import org.vl4ds4m.banking.common.entity.Money;
import org.vl4ds4m.banking.converter.entity.CurrencyRates;
import org.vl4ds4m.banking.rates.client.http.RatesApi;
import org.vl4ds4m.banking.rates.client.http.invoker.ApiClient;

import java.util.stream.Collectors;

@Slf4j
public class RatesClient {

    private final RatesApi api;

    public RatesClient(ApiClient client) {
        this.api = new RatesApi(client);
    }

    public CurrencyRates getRates() {
        log.info("Request currency rates");
        var response = api.getRates();

        var base = Currency.valueOf(response.getBase().getValue());
        var rates = response.getRates()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        e -> Currency.valueOf(e.getKey()),
                        e -> Money.of(e.getValue())));

        return new CurrencyRates(base, rates);
    }

    // TODO
    // private final RetryTemplate retryTemplate;
    // private final ObservationRegistry observationRegistry;
    /*public RatesResponse getRates2() {
        RetryCallback<RatesResponse, RatesServiceException> retryCallback =
                context -> Observation
                        .createNotStarted("rates", observationRegistry)
                        .observe(this::requestRates);
        return retryTemplate.execute(retryCallback);
    }*/

    // TODO
    /*private RatesResponse requestRates() {
        try {
            return api.getRates();
        } catch (RestClientException e) {
            throw new RatesServiceException("Service is unavailable: " + e.getMessage());
        }
    }*/
}
