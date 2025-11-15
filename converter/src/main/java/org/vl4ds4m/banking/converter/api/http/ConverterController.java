package org.vl4ds4m.banking.converter.api.http;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.vl4ds4m.banking.common.util.To;
import org.vl4ds4m.banking.converter.api.http.converter.CurrencyConverter;
import org.vl4ds4m.banking.converter.api.http.model.ConvertCurrencyResponse;
import org.vl4ds4m.banking.converter.api.http.model.Currency;
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
                CurrencyConverter.toEntity(from),
                CurrencyConverter.toEntity(to),
                To.moneyOrReject(amount, "Amount"));

        var response = new ConvertCurrencyResponse(converted.amount());
        return ResponseEntity.ok(response);
    }
}
