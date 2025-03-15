package edu.vl4ds4m.banking.accounts.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record NotificationRequest(
    @JsonProperty("customerId")
    Integer customerId,

    @JsonProperty("message")
    String message
) {
}
