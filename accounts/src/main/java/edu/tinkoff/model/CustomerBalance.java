package edu.tinkoff.model;

import java.math.BigDecimal;

public record CustomerBalance(BigDecimal balance, Currency currency) {
}
