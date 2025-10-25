package org.vl4ds4m.banking.accounts.client.util;

import org.vl4ds4m.banking.accounts.client.converter.model.Currency;

public final class CurrencyConverter {

    private CurrencyConverter() {}

    public static Currency toApi(org.vl4ds4m.banking.common.entity.Currency currency) {
        return Currency.fromValue(currency.name());
    }
}
