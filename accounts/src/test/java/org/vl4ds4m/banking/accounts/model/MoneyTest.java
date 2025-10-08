package org.vl4ds4m.banking.accounts.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MoneyTest {

    @DisplayName("Создание Money из BigDecimal")
    @ParameterizedTest(name = "Amount = {0}")
    @MethodSource("amountProvider")
    void testCreateMoney(BigDecimal amount, BigDecimal expected) {
        // Act
        var money = new Money(amount);

        // Assert
        assertEquals(expected, money.amount());
    }

    @DisplayName("Ошибка при создании Money")
    @ParameterizedTest(name = "Amount = {0}")
    @MethodSource("invalidAmountProvider")
    void testCreateMoneyFailed(BigDecimal amount) {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> new Money(amount));
    }

    @DisplayName("Сложение Money")
    @ParameterizedTest(name = "{0} + {1}")
    @MethodSource("summandsProvider")
    void testSumMoney(Money a, Money b, Money sum) {
        // Act
        var result = a.add(b);

        // Assert
        assertEquals(sum, result);
    }

    private static Stream<Arguments> amountProvider() {
        return mapArgs(o -> new BigDecimal("" + o),
                Arguments.of("0", "0.00"),
                Arguments.of("-0", "0.00"),
                Arguments.of("1", "1.00"),
                Arguments.of("10", "10.00"),
                Arguments.of("1.23", "1.23"),
                Arguments.of("0.004", "0.00"),
                Arguments.of("0.005", "0.00"),
                Arguments.of("0.007", "0.01"),
                Arguments.of("0.014", "0.01"),
                Arguments.of("0.015", "0.02"),
                Arguments.of("0.017", "0.02"),
                Arguments.of("0.0009", "0.00"),
                Arguments.of("49.847", "49.85"),
                Arguments.of("98.252", "98.25"),
                Arguments.of("79236492853.2", "79236492853.20"),
                Arguments.of("526000", "526000.00")
        );
    }

    private static Stream<BigDecimal> invalidAmountProvider() {
        return Stream.of(
                BigDecimal.ONE.negate(),
                BigDecimal.valueOf(-7345, 2),
                BigDecimal.valueOf(-1, 2),
                BigDecimal.valueOf(-1, 10)
        );
    }

    private static Stream<Arguments> summandsProvider() {
        return mapArgs(o -> new Money(new BigDecimal("" + o)),
                Arguments.of("1", "2", "3"),
                Arguments.of("3.05", "8.26", "11.31"),
                Arguments.of("4576.694", "2983.4762", "7560.17"),
                Arguments.of("4.595", "5.395", "10"),
                Arguments.of("8.725", "9.365", "18.08"),
                Arguments.of("73.115", "962.845", "1035.96")
        );
    }

    private static Stream<Arguments> mapArgs(Function<Object, Object> mapper, Arguments... argsArray) {
        return Stream.of(argsArray)
                .map(args -> Arrays.stream(args.get()))
                .map(s -> s.map(mapper))
                .map(s -> s::toArray);
    }
}
