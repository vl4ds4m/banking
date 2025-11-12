package org.vl4ds4m.banking.accounts.http.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.vl4ds4m.banking.accounts.http.client.converter.CurrencyConverter;
import org.vl4ds4m.banking.common.entity.Currency;
import org.vl4ds4m.banking.common.entity.Money;
import org.vl4ds4m.banking.converter.http.client.ConvertApi;

@Slf4j
@RequiredArgsConstructor
public class ConverterClient {

    private final ConvertApi api;

    public Money convertCurrency(Currency from, Currency to, Money money) {
        var apiFrom = CurrencyConverter.toApi(from);
        var apiTo = CurrencyConverter.toApi(to);
        var amount = money.amount();

        log.info("Request currency conversion: {}, {} -> {}", amount, from, to);
        var response = api.convertCurrency(apiFrom, apiTo, amount);

        var converted = response.getConvertedAmount();
        return Money.of(converted);
    }
}
