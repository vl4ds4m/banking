package org.vl4ds4m.banking.converter.http.api.converter;

import org.vl4ds4m.banking.converter.http.api.model.Currency;

public final class CurrencyConverter {

    private CurrencyConverter() {}

    public static org.vl4ds4m.banking.common.entity.Currency toEntity(Currency currency) {
        return org.vl4ds4m.banking.common.entity.Currency.valueOf(currency.getValue());
    }
}
