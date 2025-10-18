package org.vl4ds4m.banking.converter;

import org.slf4j.Logger;
import org.vl4ds4m.banking.converter.exception.InvalidCurrencyException;
import org.vl4ds4m.banking.converter.exception.NonPositiveAmountException;
import org.vl4ds4m.banking.rates.RatesServiceException;

public class ConverterExceptionHandler {
    private final Logger logger;

    public ConverterExceptionHandler(Logger logger) {
        this.logger = logger;
    }

    public void debugNonPositiveAmount(NonPositiveAmountException e) {
        logger.debug("Handle NonPositiveAmountException: {}", e.getMessage());
    }

    public void debugInvalidCurrency(InvalidCurrencyException e) {
        logger.debug("Handle InvalidCurrencyException: {}", e.getMessage());
    }

    public String warnRatesServiceError(RatesServiceException e) {
        logger.warn("Handle RatesServiceException: {}", e.getMessage());
        return "Rates service is unavailable";
    }
}
