package org.vl4ds4m.banking.accounts.deprecation.messaging;

import org.vl4ds4m.banking.common.entity.Currency;

import java.math.BigDecimal;

public record AccountBrokerMessage(
    Long accountNumber,
    Currency currency,
    BigDecimal balance
) {
    public static final String DESTINATION = "/accounts";
}
