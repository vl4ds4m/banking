package edu.tinkoff.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.tinkoff.model.Currency;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CurrencyMessage(
        Currency currency,
        BigDecimal amount,
        @JsonProperty("message") String errorMessage
) {
}
