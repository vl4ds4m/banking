package edu.vl4ds4m.banking.accounts.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.vl4ds4m.banking.dto.Currency;
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
