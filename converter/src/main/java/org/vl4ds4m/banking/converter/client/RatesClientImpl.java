package org.vl4ds4m.banking.converter.client;

import org.springframework.web.client.RestClientException;
import org.vl4ds4m.banking.common.entity.Currency;
import org.vl4ds4m.banking.common.entity.Money;
import org.vl4ds4m.banking.common.exception.ServiceException;
import org.vl4ds4m.banking.common.util.To;
import org.vl4ds4m.banking.converter.entity.CurrencyRates;
import org.vl4ds4m.banking.rates.openapi.client.api.RatesApi;
import org.vl4ds4m.banking.rates.openapi.client.invoke.ApiClient;
import org.vl4ds4m.banking.rates.openapi.client.model.RatesResponse;

import java.util.stream.Collectors;

public class RatesClientImpl implements RatesClient {

    private final RatesApi api;

    public RatesClientImpl(ApiClient client) {
        this.api = new RatesApi(client);
    }

    @Override
    public CurrencyRates getRates() {
        RatesResponse response;
        try {
            response = api.getRates();
        } catch (RestClientException e) {
            throw new ServiceException("rates", e.getMostSpecificCause());
        }

        var base = To.currency(response.getBase());
        var rates = response.getRates()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        e -> Currency.valueOf(e.getKey()),
                        e -> Money.of(e.getValue())));

        return new CurrencyRates(base, rates);
    }
}
