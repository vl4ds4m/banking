package org.vl4ds4m.banking.accounts.customer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record CustomerBalanceResponse(
    @JsonProperty("balance")
    BigDecimal balance,

    @JsonProperty("currency")
    String currency
) {
}
