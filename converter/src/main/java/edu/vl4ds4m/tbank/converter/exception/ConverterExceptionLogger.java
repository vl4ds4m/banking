package edu.vl4ds4m.tbank.converter.exception;

import org.slf4j.Logger;

public class ConverterExceptionLogger {
    private final Logger logger;

    public ConverterExceptionLogger(Logger logger) {
        this.logger = logger;
    }

    public void debugNonPositiveAmountMessage() {
        logger.debug("Handle NonPositiveAmountException");
    }

    public void debugInvalidCurrencyMessage(String currency) {
        logger.debug("Handle InvalidCurrencyException[currency = {}]", currency);
    }

    public void warnRatesServiceError(String message) {
        logger.warn("Exception is caused by RatesService: {}", message);
    }
}
