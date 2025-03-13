package edu.vl4ds4m.banking.converter.controller;

import edu.vl4ds4m.banking.converter.exception.ConverterExceptionHandler;
import edu.vl4ds4m.banking.converter.exception.InvalidCurrencyException;
import edu.vl4ds4m.banking.converter.exception.NonPositiveAmountException;
import edu.vl4ds4m.banking.converter.exception.RatesServiceException;
import edu.vl4ds4m.banking.converter.message.ErrorMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ConverterControllerExceptionHandler {
    private static final Logger logger =
        LoggerFactory.getLogger(ConverterControllerExceptionHandler.class);

    private static final ConverterExceptionHandler handler =
        new ConverterExceptionHandler(logger);

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage returnNonPositiveAmountMessage(NonPositiveAmountException e) {
        handler.debugNonPositiveAmount(e);
        return new ErrorMessage("Отрицательная сумма");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage returnInvalidCurrencyMessage(InvalidCurrencyException e) {
        handler.debugInvalidCurrency(e);
        return new ErrorMessage(String.format("Валюта %s недоступна", e.currency));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public void warnRatesServiceError(RatesServiceException e) {
        handler.warnRatesServiceError(e);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public void returnInternalError(Exception e) {
        handler.errorUnhandledException(e);
    }
}
