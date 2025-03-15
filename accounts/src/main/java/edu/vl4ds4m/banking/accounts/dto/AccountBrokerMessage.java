package edu.vl4ds4m.banking.accounts.dto;

import edu.vl4ds4m.banking.dto.Currency;

import java.math.BigDecimal;

public record AccountBrokerMessage(
    Integer accountNumber,
    Currency currency,
    BigDecimal balance
) {
    public static final String DESTINATION = "/topic/accounts";
}
