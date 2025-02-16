package edu.vl4ds4m.tbank.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AccountCreationResponse(
        @JsonProperty("accountNumber") int accountNumber
) {
}
