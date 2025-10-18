package org.vl4ds4m.banking.customer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CustomerCreationResponse(
    @JsonProperty("customerId")
    int customerId
) {
}
