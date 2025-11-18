package org.vl4ds4m.banking.common.util;

import org.vl4ds4m.banking.common.entity.Currency;

class GrpcCurrencyConverter {

    private GrpcCurrencyConverter() {}

    private static final String GRPC_PREFIX = "CURRENCY_";

    static org.vl4ds4m.banking.common.grpc.Currency convert(Currency currency) {
        return org.vl4ds4m.banking.common.grpc.Currency.valueOf(GRPC_PREFIX + currency.name());
    }

    static Currency convert(org.vl4ds4m.banking.common.grpc.Currency currency) {
        var name = currency.name().substring(GRPC_PREFIX.length());
        return Currency.valueOf(name);
    }
}
