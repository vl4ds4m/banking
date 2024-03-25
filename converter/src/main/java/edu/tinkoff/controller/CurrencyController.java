package edu.tinkoff.controller;

import edu.tinkoff.auth.KeycloakAuthValidator;
import edu.tinkoff.dto.CurrencyMessage;
import edu.tinkoff.model.Currency;
import edu.tinkoff.service.ConverterService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Objects;

@RestController
@RequestMapping(path = "convert", produces = MediaType.APPLICATION_JSON_VALUE)
public class CurrencyController {
    private final ConverterService converterService;
    private final KeycloakAuthValidator keycloakAuthValidator;

    public CurrencyController(ConverterService converterService, KeycloakAuthValidator keycloakAuthValidator) {
        this.converterService = converterService;
        this.keycloakAuthValidator = keycloakAuthValidator;
    }

    @GetMapping
    public ResponseEntity<CurrencyMessage> convert(
            @RequestParam("from") String fromName,
            @RequestParam("to") String toName,
            @RequestParam("amount") BigDecimal amount,
            @RequestHeader HttpHeaders headers
    ) {
        String token = Objects.requireNonNull(headers.get(HttpHeaders.AUTHORIZATION)).getFirst();

        if (!keycloakAuthValidator.validateTokens(token)) {
            return ResponseEntity.badRequest().body(new CurrencyMessage(null, null, null));
        }

        Currency from = Currency.fromValue(fromName);
        if (from == null) {
            return currencyErrorResponse(fromName);
        }

        Currency to = Currency.fromValue(toName);
        if (to == null) {
            return currencyErrorResponse(toName);
        }

        if (BigDecimal.ZERO.compareTo(amount) >= 0) {
            return invalidAmountErrorResponse();
        }

        BigDecimal convertedAmount = converterService.convert(from, to, amount);
        return ResponseEntity.ok().body(new CurrencyMessage(to, convertedAmount, "Ок"));
    }

    private ResponseEntity<CurrencyMessage> currencyErrorResponse(String currencyName) {
        return ResponseEntity.badRequest().body(new CurrencyMessage(
                null,
                null,
                "Валюта " + currencyName + " недоступна"
        ));
    }

    private ResponseEntity<CurrencyMessage> invalidAmountErrorResponse() {
        return ResponseEntity.badRequest().body(new CurrencyMessage(
                null,
                null,
                "Отрицательная сумма"
        ));
    }
}
