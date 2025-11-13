package org.vl4ds4m.banking.accounts.api.http.converter;

import org.vl4ds4m.banking.accounts.api.http.model.Currency;

public final class CurrencyConverter {

    private CurrencyConverter() {}

    public static Currency toApi(org.vl4ds4m.banking.common.entity.Currency currency) {
        return Currency.fromValue(currency.name());
    }

    public static org.vl4ds4m.banking.common.entity.Currency toEntity(Currency currency) {
        return org.vl4ds4m.banking.common.entity.Currency.valueOf(currency.getValue());
    }
}
