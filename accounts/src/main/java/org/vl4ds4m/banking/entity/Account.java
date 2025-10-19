package org.vl4ds4m.banking.entity;

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
