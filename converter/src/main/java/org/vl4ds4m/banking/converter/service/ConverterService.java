package org.vl4ds4m.banking.converter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.vl4ds4m.banking.common.entity.Currency;
import org.vl4ds4m.banking.common.entity.Money;
import org.vl4ds4m.banking.common.exception.ServiceException;
import org.vl4ds4m.banking.converter.client.RatesClient;
import org.vl4ds4m.banking.converter.entity.CurrencyRates;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConverterService {

    private final RatesClient ratesClient;

    // TODO
    // @Observed
    public Money convert(Currency source, Currency target, Money money) {
        if (money.isEmpty()) {
            log.warn("Money is zero, conversion is redundant");
            return money;
        }

        if (target.equals(source)) {
            log.warn("Source currency equals target currency, conversion is redundant");
            return money;
        }

        var currencyRates = ratesClient.getRates();
        var base = currencyRates.base();
        final Money converted;

        if (base.equals(source)) {
            var rate = getRate(currencyRates, target);
            converted = money.divide(rate);
        } else if (base.equals(target)) {
            var rate = getRate(currencyRates, source);
            converted = money.multiply(rate);
        } else {
            var rate = getRate(currencyRates, source);
            var transitive = money.multiply(rate);
            rate = getRate(currencyRates, target);
            converted = transitive.divide(rate);
        }

        log.debug("Convert {} {} to {} {}", money, source, converted, target);
        return converted;
    }

    private static Money getRate(CurrencyRates currencyRates, Currency currency) {
        var money = currencyRates.rates().get(currency);
        if (money == null || money.isEmpty()) {
            throw new ServiceException("rates", "%s rate is absent or zero".formatted(currency));
        }
        return money;
    }
}
