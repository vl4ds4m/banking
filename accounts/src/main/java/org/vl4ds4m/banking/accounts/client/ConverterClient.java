package org.vl4ds4m.banking.accounts.client;

import org.vl4ds4m.banking.common.entity.Currency;
import org.vl4ds4m.banking.common.entity.Money;

public interface ConverterClient {

    Money convertCurrency(Currency source, Currency target, Money money);
}
