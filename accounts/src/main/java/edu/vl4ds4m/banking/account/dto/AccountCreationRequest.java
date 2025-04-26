package edu.vl4ds4m.banking.account.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.vl4ds4m.banking.currency.Currency;
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
