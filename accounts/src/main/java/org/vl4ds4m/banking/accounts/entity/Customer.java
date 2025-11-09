package org.vl4ds4m.banking.accounts.entity;

import java.time.LocalDate;

public record Customer(

        String nickname,

        String forename,

        String surname,

        LocalDate birthdate
) {}
