package edu.tinkoff.dto;

import java.math.BigDecimal;

public record CustomerBalance(BigDecimal balance, Currency currency) {
}
