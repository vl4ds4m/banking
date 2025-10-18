package org.vl4ds4m.banking.account.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import org.vl4ds4m.banking.currency.Currency;

public record AccountCreationRequest(
    @JsonProperty("customerId")
    @NotNull
    Integer customerId,

    @JsonProperty("currency")
    @NotNull
    Currency currency
) {
}
