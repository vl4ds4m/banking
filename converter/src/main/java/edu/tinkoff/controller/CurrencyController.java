package edu.tinkoff.controller;

import edu.tinkoff.dto.CurrencyMessage;
import edu.tinkoff.service.ConverterService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping(path = "convert", produces = MediaType.APPLICATION_JSON_VALUE)
public class CurrencyController {
    private final ConverterService converterService;

    public CurrencyController(ConverterService converterService) {
        this.converterService = converterService;
    }

    @GetMapping
    public ResponseEntity<CurrencyMessage> convert(
            @RequestParam("from") String fromName,
            @RequestParam("to") String toName,
            @RequestParam("amount") BigDecimal amount
    ) {
        CurrencyMessage message = converterService.convert(fromName, toName, amount);
        return message.errorMessage() == null ?
                ResponseEntity.ok(message) :
                ResponseEntity.badRequest().body(message);
    }
}
