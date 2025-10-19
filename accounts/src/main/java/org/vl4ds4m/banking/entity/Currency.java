package org.vl4ds4m.banking.entity;

public enum Currency {
    USD,
    RUB,
    CNY,
    EUR,
    GBP,
    ;

    public org.vl4ds4m.banking.api.model.Currency toApiCurrency() {
        return org.vl4ds4m.banking.api.model.Currency.fromValue(name());
    }

    public static Currency valueOf(org.vl4ds4m.banking.api.model.Currency currency) {
        return valueOf(currency.getValue());
    }
}
