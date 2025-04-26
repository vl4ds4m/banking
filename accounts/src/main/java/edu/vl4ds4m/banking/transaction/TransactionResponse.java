package edu.vl4ds4m.banking.transaction;

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
