package org.vl4ds4m.banking.accounts.model;

import lombok.Value;

import java.time.LocalDate;

@Value
public class Customer {

    String name;

    String firstName;

    String lastName;

    LocalDate birthDate;
}
