package edu.vl4ds4m.banking.accounts.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CustomerCreationResponse(
        @JsonProperty("customerId")
        int customerId
) {
}
