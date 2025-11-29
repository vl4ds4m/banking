package org.vl4ds4m.banking.accounts.entity;

import java.time.LocalDate;

public record Customer(

    String login,

    String forename,

    String surname,

    LocalDate birthdate

) {}
