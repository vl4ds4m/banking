package org.vl4ds4m.banking.accounts.customer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.vl4ds4m.banking.currency.Currency;

import java.math.BigDecimal;

public record CustomerBalanceResponse(
    @JsonProperty("balance")
    BigDecimal balance,

    @JsonProperty("currency")
    Currency currency
) {
}
