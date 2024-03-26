package edu.tinkoff.model;

import java.math.BigDecimal;

public record AccountBalance(BigDecimal amount, Currency currency) {
}
