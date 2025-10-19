package org.vl4ds4m.banking.entity;

import java.time.LocalDate;

public record Customer(

        String name,

        String firstName,

        String lastName,

        LocalDate birthDate
) {
    public static String logStr(String name) {
        return "Customer[name=" + name + "]";
    }
}
