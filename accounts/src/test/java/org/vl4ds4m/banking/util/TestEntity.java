package org.vl4ds4m.banking.util;

import org.vl4ds4m.banking.entity.Account;
import org.vl4ds4m.banking.entity.Currency;
import org.vl4ds4m.banking.entity.Customer;
import org.vl4ds4m.banking.entity.Money;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TestEntity {

    private TestEntity() {}

    public static Account createDefaultAccount() {
        return new Account(
                9678345012L,
                Currency.RUB,
                Money.of(new BigDecimal("7529.83")));
    }

    public static Customer createDefaultCustomer() {
        return new Customer(
                "freerun_father",
                "Sebastien", "Foucan",
                LocalDate.of(1974, 5, 27));
    }
}
