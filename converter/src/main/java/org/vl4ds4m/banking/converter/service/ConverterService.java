package org.vl4ds4m.banking.converter.service;

import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.vl4ds4m.banking.common.entity.Currency;
import org.vl4ds4m.banking.common.entity.Money;
import org.vl4ds4m.banking.common.entity.MoneyException;
import org.vl4ds4m.banking.converter.entity.CurrencyRates;
import org.vl4ds4m.banking.converter.service.exception.NonPositiveAmountException;
import org.vl4ds4m.banking.converter.service.exception.RatesServiceException;

import java.math.BigDecimal;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConverterService {

    private final RatesService ratesService;

    @Observed
    public Money convert(Currency source, Currency target, BigDecimal amount) {
        Money money;
        try {
            money = Money.of(amount);
        } catch (MoneyException e) {
            money = Money.empty();
        }
        if (money.isEmpty()) {
            throw new NonPositiveAmountException(amount);
        }
        Money converted;
        if (target.equals(source)) {
            converted = money;
            log.debug("Return passed amount as source currency equals target currency");
        } else {
            var currencyRates = ratesService.getRates();
            var base = currencyRates.base();
            if (base.equals(source)) {
                var rate = requireRate(currencyRates, target);
                converted = money.divide(rate);
            } else if (base.equals(target)) {
                var rate = requireRate(currencyRates, source);
                converted = money.multiply(rate);
            } else {
                var rate = requireRate(currencyRates, source);
                converted = money.multiply(rate);
                rate = requireRate(currencyRates, target);
                converted = converted.divide(rate);
            }
            log.debug("Convert {} {} to {} {}", money, source, converted, target);
        }
        return converted;
    }

    private static Money requireRate(CurrencyRates rates, Currency currency) {
        var money = rates.rates().get(currency);
        if (money == null) {
            String message = String.format("'%s' rate is null", currency);
            throw new RatesServiceException(message);
        }
        return money;
    }
}
