package edu.vl4ds4m.banking.account.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record AccountTopUpRequest(
    @JsonProperty("amount")
    @NotNull
    @Positive
    BigDecimal amount
) {
}
