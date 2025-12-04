package org.vl4ds4m.banking.common.entity;

public record Transaction(

    long accountNumber,

    Money money,

    boolean withdraw

) {}
