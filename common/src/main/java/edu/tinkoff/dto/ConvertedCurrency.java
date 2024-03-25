package edu.tinkoff.dto;

import edu.tinkoff.model.Currency;

import java.math.BigDecimal;

public record ConvertedCurrency(Currency currency, BigDecimal amount) {
}
