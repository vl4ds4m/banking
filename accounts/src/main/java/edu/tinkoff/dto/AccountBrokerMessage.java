package edu.tinkoff.dto;

import java.math.BigDecimal;

public record AccountBrokerMessage(
        Integer accountNumber,
        Currency currency,
        BigDecimal balance
) {
    public static final String DESTINATION = "/topic/accounts";
}
