package edu.vl4ds4m.banking.account.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AccountCreationResponse(
    @JsonProperty("accountNumber")
    int accountNumber
) {
}
