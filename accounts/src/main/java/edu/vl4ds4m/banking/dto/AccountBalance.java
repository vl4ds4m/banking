package edu.vl4ds4m.banking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.vl4ds4m.banking.currency.Currency;

import java.math.BigDecimal;

public record AccountBalance(
    @JsonProperty("amount")
    BigDecimal amount,

    @JsonProperty("currency")
    Currency currency
) {
}
