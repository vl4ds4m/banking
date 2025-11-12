package org.vl4ds4m.banking.accounts.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.vl4ds4m.banking.accounts.http.client.ConverterClient;
import org.vl4ds4m.banking.common.entity.Currency;
import org.vl4ds4m.banking.common.entity.Money;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConverterService {

    private final ConverterClient converterClient;

    public Money convert(Currency from, Currency to, Money money) {
        if (money.isEmpty() || from.equals(to)) return money;
        return converterClient.convertCurrency(from, to, money);
    }
}
