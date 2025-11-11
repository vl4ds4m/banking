package org.vl4ds4m.banking.converter.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.vl4ds4m.banking.common.util.To;
import org.vl4ds4m.banking.converter.api.converter.CurrencyConverter;
import org.vl4ds4m.banking.converter.service.ConverterService;
import org.vl4ds4m.banking.converter.api.model.ConvertCurrencyResponse;
import org.vl4ds4m.banking.converter.api.model.Currency;
import org.vl4ds4m.banking.converter.api.model.InvalidQueryResponse;
import org.vl4ds4m.banking.converter.service.exception.RatesServiceException;

import java.math.BigDecimal;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ConverterController implements ConvertApi {

    private static final ConverterExceptionHandler handler = new ConverterExceptionHandler(log);

    private final ConverterService service;

    @Override
    public ResponseEntity<ConvertCurrencyResponse> convertCurrency(
            Currency from,
            Currency to,
            BigDecimal amount
    ) {
        log.info("Accept {} {}: {}, {} -> {}",
                HttpMethod.GET, ConvertApi.PATH_CONVERT_CURRENCY,
                amount, from, to);

        var converted = service.convert(
                CurrencyConverter.toEntity(from),
                CurrencyConverter.toEntity(to),
                To.moneyOrReject(amount, "Amount"));

        var response = new ConvertCurrencyResponse(converted.amount());
        return ResponseEntity.ok(response);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public InvalidQueryResponse warnRatesServiceError(RatesServiceException e) {
        var reason = handler.warnRatesServiceError(e);
        return new InvalidQueryResponse(reason);
    }
}
