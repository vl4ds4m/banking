package edu.vl4ds4m.banking.message;

import java.math.BigDecimal;

public record CurrencyMessage(String currency, BigDecimal amount) {
}
