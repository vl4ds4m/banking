package edu.tinkoff.controller;

import edu.tinkoff.model.Currency;
import edu.tinkoff.model.RatesResposne;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@RestController
public class CurrencyController {
    @Value("${services.currency-rates.url}")
    private String currencyRatesUrl;

    @GetMapping("/convert")
    public ResponseEntity<String> convert(
            @RequestParam("from") String fromName,
            @RequestParam("to") String toName,
            @RequestParam("amount") BigDecimal amount
    ) {
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

        BigDecimal convertedAmount = convertCurrencyAmount(from, to, amount);
        String responseBody = String.format("""
                {
                  "currency": "%s",
                  "amount": %.2f
                }
                """, to, convertedAmount);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(responseBody);
    }

    private ResponseEntity<String> currencyErrorResponse(String currencyName) {
        String responseBody = String.format("""
                {
                  "message": "Валюта %s недоступна"
                }
                """, currencyName);
        return ResponseEntity.status(400)
                .contentType(MediaType.APPLICATION_JSON)
                .body(responseBody);
    }

    private ResponseEntity<String> invalidAmountErrorResponse() {
        String responseBody = """
                {
                  "message": "Отрицательная сумма"
                }
                """;
        return ResponseEntity.status(400)
                .contentType(MediaType.APPLICATION_JSON)
                .body(responseBody);
    }

    private BigDecimal convertCurrencyAmount(Currency from, Currency to, BigDecimal amount) {
        RatesResposne ratesResposne = getRatesResponse();
        Map<String, BigDecimal> rates = ratesResposne.getRates();

        final int scale = 2;
        final RoundingMode roundingMode = RoundingMode.HALF_EVEN;

        BigDecimal convertedAmount;

        if (from == to) {
            convertedAmount = amount;
        } else if (from == ratesResposne.getBase()) {
            BigDecimal currencyValue = rates.get(to.getValue());
            convertedAmount = amount.divide(currencyValue, scale, roundingMode);
        } else if (to == ratesResposne.getBase()) {
            BigDecimal currencyValue = rates.get(from.getValue());
            convertedAmount = amount.multiply(currencyValue);
        } else {
            BigDecimal currencyValue = rates.get(from.getValue());
            convertedAmount = amount.multiply(currencyValue);
            currencyValue = rates.get(to.getValue());
            convertedAmount = convertedAmount.divide(currencyValue, scale, roundingMode);
        }

        return convertedAmount;
    }

    private RatesResposne getRatesResponse() {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(currencyRatesUrl, RatesResposne.class);
    }
}
