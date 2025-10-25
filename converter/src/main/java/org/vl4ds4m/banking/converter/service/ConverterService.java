package org.vl4ds4m.banking.converter.service;

import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.vl4ds4m.banking.common.Conversions;
import org.vl4ds4m.banking.converter.api.model.Currency;
import org.vl4ds4m.banking.converter.service.exception.NonPositiveAmountException;
import org.vl4ds4m.banking.converter.service.exception.RatesServiceException;

import java.math.BigDecimal;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConverterService {

    private final RatesService service;

    @Observed
    public BigDecimal convert(Currency source, Currency target, BigDecimal amount) {
        amount = Conversions.setScale(amount);
        if (BigDecimal.ZERO.compareTo(amount) >= 0) {
            throw new NonPositiveAmountException(amount);
        }
        BigDecimal converted;
        if (target.equals(source)) {
            converted = amount;
            log.info("Return passed amount as source currency equals target currency");
        } else {
            var ratesResponse = service.getRatesResponse();
            var base = Currency.fromValue(ratesResponse.getBase().getValue());
            Map<String, BigDecimal> rates = ratesResponse.getRates();
            BigDecimal rate;
            if (base.equals(source)) {
                rate = requireRate(rates, target);
                converted = amount.divide(rate, Conversions.SCALE, Conversions.ROUNDING_MODE);
            } else if (base.equals(target)) {
                rate = requireRate(rates, source);
                converted = amount.multiply(rate);
                converted = Conversions.setScale(converted);
            } else {
                rate = requireRate(rates, source);
                converted = amount.multiply(rate);
                rate = requireRate(rates, target);
                converted = converted.divide(rate, Conversions.SCALE, Conversions.ROUNDING_MODE);
            }
            log.info("Convert [{} {}] to [{} {}]", amount, source, converted, target);
        }
        return converted;
    }

    private static BigDecimal requireRate(Map<String, BigDecimal> rates, Currency currency) {
        BigDecimal rate = rates.get(currency.getValue());
        if (rate == null) {
            String message = String.format("'%s' rate is null", currency.getValue());
            throw new RatesServiceException(message);
        }
        return rate;
    }
}
