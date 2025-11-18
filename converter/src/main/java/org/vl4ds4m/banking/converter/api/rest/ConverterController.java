package org.vl4ds4m.banking.converter.api.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.vl4ds4m.banking.common.openapi.model.Currency;
import org.vl4ds4m.banking.common.util.To;
import org.vl4ds4m.banking.converter.openapi.server.api.ConvertApi;
import org.vl4ds4m.banking.converter.openapi.server.model.ConvertCurrencyResponse;
import org.vl4ds4m.banking.converter.service.ConverterService;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
public class ConverterController implements ConvertApi {

    private final ConverterService service;

    @Override
    public ResponseEntity<ConvertCurrencyResponse> convertCurrency(
            Currency from,
            Currency to,
            BigDecimal amount
    ) {
        var converted = service.convert(
                To.currency(from),
                To.currency(to),
                To.moneyOrReject(amount, "Amount"));

        var response = new ConvertCurrencyResponse(converted.amount());
        return ResponseEntity.ok(response);
    }
}
