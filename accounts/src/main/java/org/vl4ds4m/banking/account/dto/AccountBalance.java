package org.vl4ds4m.banking.account.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.vl4ds4m.banking.currency.Currency;

import java.math.BigDecimal;

public record AccountBalance(
    @JsonProperty("amount")
    BigDecimal amount,

    @JsonProperty("currency")
    Currency currency
) {
}
