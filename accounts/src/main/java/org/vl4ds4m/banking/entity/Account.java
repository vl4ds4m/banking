package org.vl4ds4m.banking.entity;

import lombok.Value;

@Value
public class Account {

    Long number;

    Currency currency;

    Money money;
}
