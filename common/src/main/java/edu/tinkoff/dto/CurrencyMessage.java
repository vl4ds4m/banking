package edu.tinkoff.dto;

import edu.tinkoff.model.Currency;

import java.math.BigDecimal;

public record CurrencyMessage(
        Currency currency,
        BigDecimal amount,
        String message
) {
}
