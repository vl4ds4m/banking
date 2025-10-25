package org.vl4ds4m.banking.accounts.account.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AccountCreationResponse(
    @JsonProperty("accountNumber")
    int accountNumber
) {
}
