package org.vl4ds4m.banking.accounts.entity.setting;

import org.vl4ds4m.banking.common.entity.Money;

import java.math.BigDecimal;

public final class MaxMoneyPerOpSetting implements Setting {

    public MaxMoneyPerOpSetting(String amount) {
        Money m = Money.of(new BigDecimal(amount));
        if (m.isEmpty()) {
            throw new IllegalArgumentException("Money must be positive");
        }
        this.money = m;
    }

    private final Money money;

    @Override
    public Key key() {
        return Key.MAX_MONEY_PER_OPERATION;
    }

    @Override
    public String value() {
        return money.amount().toString();
    }

    public Money money() {
        return money;
    }
}
