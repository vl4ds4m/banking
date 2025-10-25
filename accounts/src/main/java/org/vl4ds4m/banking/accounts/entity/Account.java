package org.vl4ds4m.banking.accounts.entity;

import org.vl4ds4m.banking.common.entity.Currency;
import org.vl4ds4m.banking.common.entity.Money;

public record Account (

        long number,

        Currency currency,

        Money money
) {
    public static String logStr(long number) {
        return "Account[number=" + number + "]";
    }

    public static String logStr(String customerName, Currency currency) {
        return "Account[customerName=" + customerName + ",currency=" + currency + "]";
    }
}
