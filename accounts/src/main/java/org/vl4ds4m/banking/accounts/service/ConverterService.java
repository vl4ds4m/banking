package org.vl4ds4m.banking.accounts.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.vl4ds4m.banking.accounts.client.converter.ConvertApi;
import org.vl4ds4m.banking.common.entity.Currency;
import org.vl4ds4m.banking.common.entity.Money;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConverterService {

    private final ConvertApi converterClient;

    public Money convert(Currency from, Currency to, Money money) {
        var apiFrom = org.vl4ds4m.banking.accounts.client.converter.model.Currency.fromValue(from.name());
        var apiTo = org.vl4ds4m.banking.accounts.client.converter.model.Currency.fromValue(to.name());
        var amount = money.amount();

        log.info("Request currency conversion: {}, {} -> {}", amount, from, to);
        var response = converterClient.convertCurrency(apiFrom, apiTo, amount);

        var converted = response.getConvertedAmount();
        return Money.of(converted);
    }
}
