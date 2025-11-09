package org.vl4ds4m.banking.accounts.entity;

import org.vl4ds4m.banking.common.entity.Currency;
import org.vl4ds4m.banking.common.entity.Money;

public record Account (

        long number,

        Currency currency,

        Money money
) {}
