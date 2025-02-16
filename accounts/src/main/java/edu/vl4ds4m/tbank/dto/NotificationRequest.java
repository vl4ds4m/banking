package edu.vl4ds4m.tbank.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record NotificationRequest(
        @JsonProperty("customerId") Integer customerId,
        @JsonProperty("message") String message
) {
}
