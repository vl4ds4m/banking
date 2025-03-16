package edu.vl4ds4m.banking.dto;

import edu.vl4ds4m.banking.currency.Currency;

import java.math.BigDecimal;

public record AccountBrokerMessage(
    Integer accountNumber,
    Currency currency,
    BigDecimal balance
) {
    public static final String DESTINATION = "/topic/accounts";
}
