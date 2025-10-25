package org.vl4ds4m.banking.accounts.account.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record AccountBalance(
    @JsonProperty("amount")
    BigDecimal amount,

    @JsonProperty("currency")
    String currency
) {
}
