package org.vl4ds4m.banking.accounts.entity.setting;

import lombok.Getter;
import org.vl4ds4m.banking.common.entity.Money;

import java.math.BigDecimal;

public final class TransferFeeSetting implements Setting {

    private final int fee;

    @Getter
    private final Money factor;

    public TransferFeeSetting(String fee) {
        int i = Integer.parseInt(fee);
        if (i < 0 || i > 99) {
            throw new IllegalArgumentException("Fee must be in range [0; 99]");
        }
        this.fee = i;
        this.factor = Money.of(BigDecimal.valueOf(i, 2));
    }

    @Override
    public Key key() {
        return Key.TRANSFER_FEE;
    }

    @Override
    public String value() {
        return "" + fee;
    }
}
