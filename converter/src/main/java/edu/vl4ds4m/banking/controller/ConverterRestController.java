package edu.vl4ds4m.banking.controller;

import edu.vl4ds4m.banking.service.ConverterService;
import edu.vl4ds4m.banking.message.CurrencyMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
public class ConverterRestController {
    private static final Logger logger = LoggerFactory.getLogger(ConverterRestController.class);

    private final ConverterService service;

    public ConverterRestController(ConverterService service) {
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
}
