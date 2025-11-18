package org.vl4ds4m.banking.common.util;

import org.vl4ds4m.banking.common.entity.Currency;

class HttpCurrencyConverter {

    private HttpCurrencyConverter() {}

    static org.vl4ds4m.banking.common.api.http.model.Currency convert(Currency currency) {
        return org.vl4ds4m.banking.common.api.http.model.Currency.fromValue(currency.name());
    }

    static Currency convert(org.vl4ds4m.banking.common.api.http.model.Currency currency) {
        return Currency.valueOf(currency.getValue());
    }
}
