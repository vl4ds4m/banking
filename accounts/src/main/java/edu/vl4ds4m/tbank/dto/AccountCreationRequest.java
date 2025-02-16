package edu.vl4ds4m.tbank.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public record AccountCreationRequest(
        @JsonProperty("customerId")
        @NotNull
        Integer customerId,

        @JsonProperty("currency")
        @NotNull
        Currency currency
) {
}
