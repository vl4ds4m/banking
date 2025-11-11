package org.vl4ds4m.banking.converter.entity;

import org.vl4ds4m.banking.common.entity.Currency;
import org.vl4ds4m.banking.common.entity.Money;

import java.util.Map;

public record CurrencyRates(

        Currency base,

        Map<Currency, Money> rates
) {}
