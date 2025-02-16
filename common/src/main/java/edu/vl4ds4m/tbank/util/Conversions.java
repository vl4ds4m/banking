package edu.vl4ds4m.tbank.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Conversions {
    private Conversions() {
    }

    public static final int SCALE = 2;
    public static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_EVEN;

    public static BigDecimal setScale(double number) {
        return BigDecimal.valueOf(number).setScale(SCALE, ROUNDING_MODE);
    }

    public static BigDecimal setScale(BigDecimal number) {
        return number.setScale(SCALE, ROUNDING_MODE);
    }
}
