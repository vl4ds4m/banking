package org.vl4ds4m.banking.converter.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.vl4ds4m.banking.common.entity.Currency;
import org.vl4ds4m.banking.common.entity.Money;
import org.vl4ds4m.banking.converter.client.http.RatesClient;
import org.vl4ds4m.banking.converter.entity.CurrencyRates;
import org.vl4ds4m.banking.converter.service.exception.RatesServiceException;
import org.vl4ds4m.banking.rates.client.http.model.RatesResponse;

import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RatesService {

    private final RatesClient ratesClient;

    public CurrencyRates getRates() {
        var response = ratesClient.getRates();
        checkResponse(response);

        var base = Currency.valueOf(response.getBase().getValue());
        var rates = response.getRates()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        e -> Currency.valueOf(e.getKey()),
                        e -> Money.of(e.getValue())));

        return new CurrencyRates(base, rates);
    }

    private static void checkResponse(RatesResponse response) {
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
