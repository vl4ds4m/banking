package edu.vl4ds4m.banking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CustomerCreationResponse(
        @JsonProperty("customerId")
        int customerId
) {
}
