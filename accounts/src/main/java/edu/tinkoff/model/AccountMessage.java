package edu.tinkoff.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record AccountMessage(
        Integer customerId,
        Currency currency,
        Integer accountNumber
) {
}
