package edu.tinkoff.controller;

import edu.tinkoff.auth.KeycloakAuthValidator;
import edu.tinkoff.dto.CurrencyMessage;
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
    public ResponseEntity<CurrencyMessage> convert(
            @RequestParam("from") String fromName,
            @RequestParam("to") String toName,
            @RequestParam("amount") BigDecimal amount,
            @RequestHeader HttpHeaders headers
    ) {
        List<String> tokens = headers.get(HttpHeaders.AUTHORIZATION);
        String token = tokens != null ? tokens.getFirst() : null;

        if (!keycloakAuthValidator.validateTokens(token)) {
            return ResponseEntity.badRequest().build();
        }

        CurrencyMessage message = converterService.convert(fromName, toName, amount);
        return message.errorMessage() == null ?
                ResponseEntity.ok(message) :
                ResponseEntity.badRequest().body(message);
    }
}
