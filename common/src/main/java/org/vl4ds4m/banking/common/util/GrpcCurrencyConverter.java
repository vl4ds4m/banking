package org.vl4ds4m.banking.common.util;

import org.vl4ds4m.banking.common.grpc.Currency;

class GrpcCurrencyConverter {

    private GrpcCurrencyConverter() {}

    private static final String GRPC_PREFIX = "CURRENCY_";

    static Currency convert(org.vl4ds4m.banking.common.entity.Currency currency) {
        return Currency.valueOf(GRPC_PREFIX + currency.name());
    }

    static org.vl4ds4m.banking.common.entity.Currency convert(Currency currency) {
        var name = currency.name().substring(GRPC_PREFIX.length());
        return org.vl4ds4m.banking.common.entity.Currency.valueOf(name);
    }
}
