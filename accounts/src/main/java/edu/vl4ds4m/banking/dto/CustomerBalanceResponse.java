package edu.vl4ds4m.banking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.vl4ds4m.banking.currency.Currency;

import java.math.BigDecimal;

public record CustomerBalanceResponse(
    @JsonProperty("balance")
    BigDecimal balance,

    @JsonProperty("currency")
    Currency currency
) {
}
