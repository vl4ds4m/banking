package org.vl4ds4m.banking;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Conversions {
    private Conversions() {}

    public static final int PRECISION = 100;
    public static final int SCALE = 2;
    public static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_EVEN;
    public static final BigDecimal ZERO = setScale(BigDecimal.ZERO);

    public static BigDecimal setScale(BigDecimal number) {
        return number.setScale(SCALE, ROUNDING_MODE);
    }

    public static BigDecimal setScale(double number) {
        return setScale(BigDecimal.valueOf(number));
    }

    public static BigDecimal setScale(String number) {
        return setScale(new BigDecimal(number));
    }
}
