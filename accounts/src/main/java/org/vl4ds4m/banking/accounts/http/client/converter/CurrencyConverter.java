package org.vl4ds4m.banking.accounts.http.client.converter;

import org.vl4ds4m.banking.converter.http.client.model.Currency;

public final class CurrencyConverter {

    private CurrencyConverter() {}

    public static Currency toApi(org.vl4ds4m.banking.common.entity.Currency currency) {
        return Currency.fromValue(currency.name());
    }
}
