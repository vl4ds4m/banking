package edu.tinkoff.controller;

import edu.tinkoff.auth.KeycloakAuthValidator;
import edu.tinkoff.dto.ConvertedCurrency;
import edu.tinkoff.dto.InvalidCurrencyMessage;
import edu.tinkoff.model.Currency;
import edu.tinkoff.service.ConverterService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

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
    public ResponseEntity<Object> convert(
            @RequestParam("from") String fromName,
            @RequestParam("to") String toName,
            @RequestParam("amount") BigDecimal amount,
            @RequestHeader HttpHeaders headers
    ) {
        List<String> tokens = headers.get(HttpHeaders.AUTHORIZATION);

        if (!keycloakAuthValidator.validateTokens(tokens)) {
            return ResponseEntity.badRequest().body("Not allowed");
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
        return ResponseEntity.ok().body(new ConvertedCurrency(to, convertedAmount));
    }

    private ResponseEntity<Object> currencyErrorResponse(String currencyName) {
        return ResponseEntity.status(400)
                .body(new InvalidCurrencyMessage("Валюта " + currencyName + " недоступна"));
    }

    private ResponseEntity<Object> invalidAmountErrorResponse() {
        return ResponseEntity.status(400)
                .body(new InvalidCurrencyMessage("Отрицательная сумма"));
    }
}
