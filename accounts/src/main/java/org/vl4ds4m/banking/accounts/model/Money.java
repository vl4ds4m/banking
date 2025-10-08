package org.vl4ds4m.banking.accounts.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record Money(BigDecimal amount) implements Comparable<Money> {

    public static final Money ZERO;

    private static final int SCALE = 2;

    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_EVEN;

    static {
        ZERO = new Money(BigDecimal.ZERO);
    }

    private static BigDecimal round(BigDecimal amount) {
        if (amount.scale() == SCALE) return amount;
        return amount.setScale(SCALE, ROUNDING_MODE);
    }

    public Money(BigDecimal amount) {
        if (BigDecimal.ZERO.compareTo(amount) > 0) {
            throw new IllegalArgumentException("Amount must be zero or positive");
        }
        this.amount = round(amount);
    }

    public Money add(Money augend) {
        var sum = this.amount.add(augend.amount);
        return new Money(sum);
    }

    @Override
    public int compareTo(Money o) {
        return this.amount.compareTo(o.amount);
    }

    @Override
    public String toString() {
        return "M[" + amount + "]";
    }
}
