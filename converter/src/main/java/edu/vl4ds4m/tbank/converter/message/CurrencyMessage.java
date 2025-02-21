package edu.vl4ds4m.tbank.converter.message;

import java.math.BigDecimal;

public record CurrencyMessage(String currency, BigDecimal amount) {
}
