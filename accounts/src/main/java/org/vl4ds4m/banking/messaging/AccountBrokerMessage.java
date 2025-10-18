package org.vl4ds4m.banking.messaging;

import org.vl4ds4m.banking.currency.Currency;

import java.math.BigDecimal;

public record AccountBrokerMessage(
    Integer accountNumber,
    Currency currency,
    BigDecimal balance
) {
    public static final String DESTINATION = "/accounts";
}
