package edu.vl4ds4m.banking.accounts.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AccountCreationResponse(
        @JsonProperty("accountNumber") int accountNumber
) {
}
