package edu.tinkoff.controller;

import edu.tinkoff.auth.KeycloakAuthClient;
import edu.tinkoff.auth.KeycloakAuthValidator;
import edu.tinkoff.model.Currency;
import edu.tinkoff.model.RatesResposne;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

@RestController
public class CurrencyController {
    @Value("${services.currency-rates.url}")
    private String currencyRatesUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private KeycloakAuthClient keycloakAuthClient;

    @Autowired
    private KeycloakAuthValidator keycloakAuthValidator;

    @GetMapping("/convert")
    public ResponseEntity<String> convert(
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

        String convertedAmount = convertCurrencyAmount(from, to, amount).toString();
        String responseBody = String.format("""
                {
                  "currency": "%s",
                  "amount": %s
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
            convertedAmount = amount.setScale(scale, roundingMode);
        } else if (from == ratesResposne.getBase()) {
            BigDecimal currencyValue = rates.get(to.getValue());
            convertedAmount = amount.divide(currencyValue, scale, roundingMode);
        } else if (to == ratesResposne.getBase()) {
            BigDecimal currencyValue = rates.get(from.getValue());
            convertedAmount = amount.multiply(currencyValue).setScale(scale, roundingMode);
        } else {
            BigDecimal currencyValue = rates.get(from.getValue());
            convertedAmount = amount.multiply(currencyValue);
            currencyValue = rates.get(to.getValue());
            convertedAmount = convertedAmount.divide(currencyValue, scale, roundingMode);
        }

        return convertedAmount;
    }

    private RatesResposne getRatesResponse() {
        String token = keycloakAuthClient.getToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        ResponseEntity<RatesResposne> response = restTemplate.exchange(
                currencyRatesUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                RatesResposne.class
        );

        return response.getBody();
    }

    private RatesResposne get_RatesResponse() {
        RatesResposne rates = new RatesResposne();
        rates.setBase(Currency.RUB);
        rates.setRates(Map.of(
                Currency.RUB.getValue(), BigDecimal.ONE,
                Currency.USD.getValue(), BigDecimal.valueOf(2.0),
                Currency.EUR.getValue(), BigDecimal.valueOf(3.0),
                Currency.GBP.getValue(), BigDecimal.valueOf(4.0),
                Currency.CNY.getValue(), BigDecimal.valueOf(5.0)
        ));
        return rates;
    }
}
