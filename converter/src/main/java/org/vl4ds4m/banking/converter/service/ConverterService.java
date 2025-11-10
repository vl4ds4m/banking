package org.vl4ds4m.banking.converter.service;

import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.vl4ds4m.banking.common.entity.Currency;
import org.vl4ds4m.banking.common.entity.Money;
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
        if (!Money.isValid(amount) || Money.of(amount).isEmpty()) {
            throw new NonPositiveAmountException(amount);
        }
        var money = Money.of(amount);

        if (target.equals(source)) {
            log.warn("Source currency equals target currency, conversion is redundant");
            return money;
        }

        var currencyRates = ratesService.getRates();
        var base = currencyRates.base();
        Money converted;

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
        return converted;
    }

    private static Money requireRate(CurrencyRates rates, Currency currency) {
        var money = rates.rates().get(currency);
        if (money == null || money.isEmpty()) {
            String message = String.format("%s rate is null or empty", currency);
            throw new RatesServiceException(message);
        }
        return money;
    }
}
