package edu.tinkoff.controller;

import edu.tinkoff.dto.CurrencyMessage;
import edu.tinkoff.service.ConverterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "convert", produces = MediaType.APPLICATION_JSON_VALUE)
public class CurrencyController {
    private static final Logger log = LoggerFactory.getLogger(CurrencyController.class);

    private final ConverterService converterService;

    public CurrencyController(ConverterService converterService) {
        this.converterService = converterService;
    }

    @GetMapping
    public ResponseEntity<CurrencyMessage> convert(
            @RequestParam("from") String fromName,
            @RequestParam("to") String toName,
            @RequestParam("amount") double amount
    ) {
        log.info("Accept a request to convert currency");
        CurrencyMessage message = converterService.convert(fromName, toName, amount);
        return message.errorMessage() == null ?
                ResponseEntity.ok(message) :
                ResponseEntity.badRequest().body(message);
    }
}
