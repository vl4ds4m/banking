package org.vl4ds4m.banking.accounts.customer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CustomerCreationResponse(
    @JsonProperty("customerId")
    int customerId
) {
}
