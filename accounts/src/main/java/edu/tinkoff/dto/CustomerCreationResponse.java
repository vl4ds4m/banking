package edu.tinkoff.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CustomerCreationResponse(
        @JsonProperty("customerId") int customerId
) {
}
