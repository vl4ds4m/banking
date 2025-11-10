package org.vl4ds4m.banking.accounts.deprecation.transaction;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.UUID;

public record TransactionResponse(
    @JsonProperty("transactionId")
    UUID uuid,

    @JsonProperty("amount")
    BigDecimal amount
) {
}
