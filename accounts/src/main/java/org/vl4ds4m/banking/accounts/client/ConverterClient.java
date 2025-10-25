package org.vl4ds4m.banking.accounts.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.vl4ds4m.banking.accounts.client.converter.ConvertApi;
import org.vl4ds4m.banking.accounts.client.converter.model.ConvertCurrencyResponse;
import org.vl4ds4m.banking.accounts.client.converter.model.Currency;

import java.math.BigDecimal;

@Slf4j
@RequiredArgsConstructor
public class ConverterClient {

    private final ConvertApi api;

    public ConvertCurrencyResponse convertCurrency(Currency from, Currency to, BigDecimal amount) {
        log.info("Request currency conversion: {}, {} -> {}", amount, from, to);
        return api.convertCurrency(from, to, amount);
    }
}
