package org.vl4ds4m.banking.accounts.api.util;

import org.vl4ds4m.banking.common.entity.Currency;

public final class CurrencyConverter {

    private CurrencyConverter() {}

    public static org.vl4ds4m.banking.accounts.api.model.Currency toApi(Currency currency) {
        return org.vl4ds4m.banking.accounts.api.model.Currency.fromValue(currency.name());
    }

    public static Currency toEntity(org.vl4ds4m.banking.accounts.api.model.Currency currency) {
        return Currency.valueOf(currency.getValue());
    }
}
