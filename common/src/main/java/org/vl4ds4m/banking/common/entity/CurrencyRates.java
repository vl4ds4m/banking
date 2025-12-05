package org.vl4ds4m.banking.common.entity;

import java.util.Map;

public record CurrencyRates(

    Currency base,

    Map<Currency, Money> rates

) {}
