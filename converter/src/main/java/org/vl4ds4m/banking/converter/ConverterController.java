package org.vl4ds4m.banking.converter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.vl4ds4m.banking.converter.exception.InvalidCurrencyException;
import org.vl4ds4m.banking.converter.exception.NonPositiveAmountException;
import org.vl4ds4m.banking.converter.message.CurrencyMessage;
import org.vl4ds4m.banking.converter.message.ErrorMessage;
import org.vl4ds4m.banking.rates.RatesServiceException;

import java.math.BigDecimal;

@RestController
public class ConverterController {
    private static final Logger logger = LoggerFactory.getLogger(ConverterController.class);

    private static final ConverterExceptionHandler handler =
        new ConverterExceptionHandler(logger);

    private final ConverterService service;

    public ConverterController(ConverterService service) {
        this.service = service;
    }

    @GetMapping("/convert")
    public CurrencyMessage convert(
        @RequestParam("from") String source,
        @RequestParam("to") String target,
        @RequestParam("amount") BigDecimal amount
    ) {
        logger.debug("Accept GET /convert?from={}&to={}&amount={}", source, target, amount);
        BigDecimal converted = service.convert(source, target, amount);
        return new CurrencyMessage(target, converted);
    }

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
}
