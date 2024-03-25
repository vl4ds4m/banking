package edu.tinkoff.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import edu.tinkoff.model.Currency;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CurrencyMessage(
        Currency currency,
        BigDecimal amount,
        String message
) {
}
