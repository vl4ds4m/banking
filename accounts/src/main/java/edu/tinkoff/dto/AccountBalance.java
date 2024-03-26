package edu.tinkoff.dto;

import java.math.BigDecimal;

public record AccountBalance(BigDecimal amount, Currency currency) {
}
