package org.vl4ds4m.banking.common.entity;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class Money implements Comparable<Money> {

    private static final Money ZERO;

    private static final int SCALE = 2;

    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_EVEN;

    static {
        ZERO = new Money(round(BigDecimal.ZERO));
    }

    private final BigDecimal amount;

    public static Money empty() {
        return Money.ZERO;
    }

    public static Money of(BigDecimal amount) {
        var zero = Money.ZERO.amount();
        if (zero.compareTo(amount) > 0) {
            throw new IllegalArgumentException("Amount must be zero or positive");
        }
        var rounded = round(amount);
        if (zero.compareTo(rounded) == 0) {
            return Money.ZERO;
        }
        return new Money(rounded);
    }

    private static BigDecimal round(BigDecimal amount) {
        if (amount.scale() == SCALE) return amount;
        return amount.setScale(SCALE, ROUNDING_MODE);
    }

    private Money(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal amount() {
        return amount;
    }

    public boolean isEmpty() {
        return compareTo(Money.ZERO) == 0;
    }

    public Money add(Money augend) {
        var sum = this.amount.add(augend.amount);
        return Money.of(sum);
    }

    public Money subtract(Money subtrahend) {
        if (this.compareTo(subtrahend) < 0) {
            throw new IllegalArgumentException("Subtrahend must be less or equal this amount");
        }
        var sub = this.amount.subtract(subtrahend.amount);
        return Money.of(sub);
    }

    public Money multiply(Money multiplicand) {
        var prod = this.amount.multiply(multiplicand.amount);
        return Money.of(prod);
    }

    public Money divide(Money divisor) {
        if (divisor.isEmpty()) {
            throw new IllegalArgumentException("Divisor must be positive");
        }
        var div = this.amount.divide(divisor.amount, SCALE, ROUNDING_MODE);
        return Money.of(div);
    }

    @Override
    public int compareTo(Money o) {
        return this.amount.compareTo(o.amount);
    }

    @Override
    public int hashCode() {
        return amount.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (this == obj) return true;
        return obj instanceof Money money
                && this.amount.equals(money.amount);
    }

    @Override
    public String toString() {
        return "M[" + amount + "]";
    }
}
