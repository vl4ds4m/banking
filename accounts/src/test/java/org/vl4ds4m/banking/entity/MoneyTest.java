package org.vl4ds4m.banking.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

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
        var e = assertThrows(MoneyException.class, () -> new Money(amount));
        assertEquals("Amount must be zero or positive", e.getMessage());
    }

    @DisplayName("Сравнение Money")
    @ParameterizedTest(name = "{0} <=> {1}")
    @MethodSource("comparingProvider")
    void testMoneyComparing(Money a, Money b, int cmp) {
        // Act
        int result = a.compareTo(b);

        // Assert
        if (cmp < 0) {
            assertTrue(result < 0);
        } else if (cmp > 0) {
            assertTrue(result > 0);
        } else {
            assertEquals(0, result);
        }
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

    @DisplayName("Вычитание Money")
    @ParameterizedTest(name = "{0} - {1}")
    @MethodSource("subtractProvider")
    void testSubtractMoney(Money a, Money b, Money sub) {
        // Act
        var result = a.subtract(b);

        // Assert
        assertEquals(sub, result);
    }

    @DisplayName("Ошибка при вычитании большего Money из меньшего")
    @Test
    void testSubtractMoneyWithDebtFailed() {
        // Arrange
        var a = new Money(new BigDecimal("63.78"));
        var b = new Money(new BigDecimal("84.02"));

        // Act & Assert
        var e = assertThrows(MoneyException.class, () -> a.subtract(b));
        assertEquals("Subtrahend must be less or equal this amount", e.getMessage());
    }

    @DisplayName("Умножение Money")
    @ParameterizedTest(name = "{0} * {1}")
    @MethodSource("multipliersProvider")
    void testMultiplyMoney(Money a, Money b, Money prod) {
        // Act
        var result = a.multiply(b);

        // Assert
        assertEquals(prod, result);
    }

    @DisplayName("Деление Money")
    @ParameterizedTest(name = "{0} / {1}")
    @MethodSource("divisionProvider")
    void testDivideMoney(Money a, Money b, Money div) {
        // Act
        var result = a.divide(b);

        // Assert
        assertEquals(div, result);
    }

    @DisplayName("Ошибка при делении на нуль-Money")
    @Test
    void testDivideMoneyToZeroFailed() {
        // Arrange
        var a = new Money(new BigDecimal("65.12"));
        var b = Money.ZERO;

        // Act & Assert
        var e = assertThrows(MoneyException.class, () -> a.divide(b));
        assertEquals("Divisor must be positive", e.getMessage());
    }

    private static Stream<Arguments> amountProvider() {
        return mapArgs((String s) -> new BigDecimal(s),
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

    private static Stream<Arguments> comparingProvider() {
        return Stream.of(
                List.of("1", "2", -1),
                List.of("2", "1", 1),
                List.of("123", "123", 0),
                List.of("1.869324", "1.86754", 0),
                List.of("1.86275", "1.86175", 0),
                List.of("1.865", "1.866", -1),
                List.of("1.865", "1.864", 0),
                List.of("1.875", "1.876", 0),
                List.of("1.875", "1.874", 1)
        ).map(list -> Arguments.of(
                new Money(new BigDecimal("" + list.get(0))),
                new Money(new BigDecimal("" + list.get(1))),
                list.get(2)
        ));
    }

    private static Stream<Arguments> summandsProvider() {
        return mapArgs(stringToMoney(),
                Arguments.of("1", "2", "3"),
                Arguments.of("7.32", "0", "7.32"),
                Arguments.of("3.05", "8.26", "11.31"),
                Arguments.of("4576.694", "2983.4762", "7560.17"),
                Arguments.of("4.595", "5.395", "10"),
                Arguments.of("8.725", "9.365", "18.08"),
                Arguments.of("73.115", "962.845", "1035.96")
        );
    }

    private static Stream<Arguments> subtractProvider() {
        return mapArgs(stringToMoney(),
                Arguments.of("3", "1", "2"),
                Arguments.of("95.25", "0", "95.25"),
                Arguments.of("78.61", "78.61", "0"),
                Arguments.of("52.875", "52.879", "0"),
                Arguments.of("7.03", "2.56", "4.47"),
                Arguments.of("8945.683", "2738.147", "6207.53"),
                Arguments.of("5.395", "4.585", "0.82"),
                Arguments.of("9.365", "8.735", "0.62"),
                Arguments.of("962.845", "73.125", "889.72")
        );
    }

    private static Stream<Arguments> multipliersProvider() {
        return mapArgs(stringToMoney(),
                Arguments.of("1", "2", "2"),
                Arguments.of("6.32", "0", "0"),
                Arguments.of("71.96", "69.12", "4973.88"),
                Arguments.of("3", "0.33333", "0.99"),
                Arguments.of("3", "0.66666", "2.01")
        );
    }

    private static Stream<Arguments> divisionProvider() {
        return mapArgs(stringToMoney(),
                Arguments.of("1", "2", "0.5"),
                Arguments.of("0", "8.91", "0"),
                Arguments.of("69341.02", "412.97", "167.91"),
                Arguments.of("7", "3", "2.33"),
                Arguments.of("11", "7", "1.57"),
                Arguments.of("0.01", "123", "0"),
                Arguments.of("2.5", "4", "0.62")
        );
    }

    @SuppressWarnings("unchecked")
    private static <T> Stream<Arguments> mapArgs(
            Function<? super T, ?> mapper,
            Arguments... argsArray
    ) {
        return Stream.of(argsArray)
                .map(args -> Arrays.stream(args.get()))
                .map(s -> s.map(o -> mapper.apply((T) o)))
                .map(l -> l::toArray);
    }

    private static Function<String, Money> stringToMoney() {
        return s -> new Money(new BigDecimal(s));
    }
}
