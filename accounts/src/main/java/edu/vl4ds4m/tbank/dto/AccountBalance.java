package edu.vl4ds4m.tbank.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record AccountBalance(
        @JsonProperty("amount") BigDecimal amount,
        @JsonProperty("currency") Currency currency
) {
}
