package edu.vl4ds4m.banking.converter.controller;

import edu.vl4ds4m.banking.converter.service.ConverterService;
import edu.vl4ds4m.banking.converter.message.CurrencyMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
public class ConverterController {
    private static final Logger logger = LoggerFactory.getLogger(ConverterController.class);

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
}
