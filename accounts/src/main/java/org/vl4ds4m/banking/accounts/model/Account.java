package org.vl4ds4m.banking.accounts.model;

import lombok.Value;

@Value
public class Account {

    Long number;

    Currency currency;

    Money money;
}
