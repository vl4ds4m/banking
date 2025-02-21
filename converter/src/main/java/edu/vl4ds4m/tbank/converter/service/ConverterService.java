package edu.vl4ds4m.tbank.converter.service;

import edu.vl4ds4m.tbank.converter.exception.InvalidCurrencyException;
import edu.vl4ds4m.tbank.converter.exception.NonPositiveAmountException;
import edu.vl4ds4m.tbank.converter.exception.RatesServiceException;
import edu.vl4ds4m.tbank.dto.Currency;
import edu.vl4ds4m.tbank.dto.RatesResponse;
import edu.vl4ds4m.tbank.util.Conversions;
import io.micrometer.observation.annotation.Observed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class ConverterService {
    private static final Logger logger = LoggerFactory.getLogger(ConverterService.class);

    private final RatesService service;

    public ConverterService(RatesService service) {
        this.service = service;
    }

    @Observed
    public BigDecimal convert(String sourceValue, String targetValue, BigDecimal amount) {
        Currency source = Currency.fromValue(sourceValue);
        if (source == null) {
            throw new InvalidCurrencyException(sourceValue);
        }
        Currency target = Currency.fromValue(targetValue);
        if (target == null) {
            throw new InvalidCurrencyException(targetValue);
        }
        amount = Conversions.setScale(amount);
        if (BigDecimal.ZERO.compareTo(amount) >= 0) {
            throw new NonPositiveAmountException();
        }
        return convert(source, target, amount);
    }

    private BigDecimal convert(Currency source, Currency target, BigDecimal amount) {
        BigDecimal converted;
        if (target.equals(source)) {
            converted = amount;
            logger.debug("Return passed amount as source currency equals target currency");
        } else {
            RatesResponse ratesResponse = service.getRatesResponse();
            Currency base = ratesResponse.getBase();
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
            logger.debug("Convert [{} {}] to [{} {}]", amount, source, converted, target);
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
