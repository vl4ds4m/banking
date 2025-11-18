package org.vl4ds4m.banking.common.util;

import org.vl4ds4m.banking.common.entity.Currency;

class RestCurrencyConverter {

    private RestCurrencyConverter() {}

    static org.vl4ds4m.banking.common.openapi.model.Currency convert(Currency currency) {
        return org.vl4ds4m.banking.common.openapi.model.Currency.fromValue(currency.name());
    }

    static Currency convert(org.vl4ds4m.banking.common.openapi.model.Currency currency) {
        return Currency.valueOf(currency.getValue());
    }
}
