package edu.vl4ds4m.tbank.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CustomerCreationResponse(
        @JsonProperty("customerId")
        int customerId
) {
}
