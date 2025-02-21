package edu.vl4ds4m.tbank.converter.controller;

import edu.vl4ds4m.tbank.converter.exception.ConverterExceptionLogger;
import edu.vl4ds4m.tbank.converter.exception.InvalidCurrencyException;
import edu.vl4ds4m.tbank.converter.exception.NonPositiveAmountException;
import edu.vl4ds4m.tbank.converter.exception.RatesServiceException;
import edu.vl4ds4m.tbank.converter.message.ErrorMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ConverterRestControllerExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(ConverterRestControllerExceptionHandler.class);
    private static final ConverterExceptionLogger dedicatedLogger = new ConverterExceptionLogger(logger);

    @ExceptionHandler(NonPositiveAmountException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage returnNonPositiveAmountMessage() {
        dedicatedLogger.debugNonPositiveAmountMessage();
        return new ErrorMessage("Отрицательная сумма");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage returnInvalidCurrencyMessage(InvalidCurrencyException e) {
        dedicatedLogger.debugInvalidCurrencyMessage(e.currency);
        String message = String.format("Валюта %s недоступна", e.currency);
        return new ErrorMessage(message);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public void warnRatesServiceError(RatesServiceException e) {
        dedicatedLogger.warnRatesServiceError(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public void returnInternalError(Exception e) {
        logger.error(e.getMessage());
    }
}
