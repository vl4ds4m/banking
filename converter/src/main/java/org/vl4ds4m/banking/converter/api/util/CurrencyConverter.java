package org.vl4ds4m.banking.converter.api.util;

import org.vl4ds4m.banking.converter.api.model.Currency;

public final class CurrencyConverter {

    private CurrencyConverter() {}

    public static org.vl4ds4m.banking.common.entity.Currency toEntity(Currency currency) {
        return org.vl4ds4m.banking.common.entity.Currency.valueOf(currency.getValue());
    }
}
