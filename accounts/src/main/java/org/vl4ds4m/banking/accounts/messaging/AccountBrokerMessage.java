package org.vl4ds4m.banking.accounts.messaging;

import org.vl4ds4m.banking.accounts.entity.Currency;

import java.math.BigDecimal;

public record AccountBrokerMessage(
    Long accountNumber,
    Currency currency,
    BigDecimal balance
) {
    public static final String DESTINATION = "/accounts";
}
