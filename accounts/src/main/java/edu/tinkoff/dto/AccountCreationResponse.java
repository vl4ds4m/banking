package edu.tinkoff.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AccountCreationResponse(
        @JsonProperty("accountNumber") int accountNumber
) {
}
