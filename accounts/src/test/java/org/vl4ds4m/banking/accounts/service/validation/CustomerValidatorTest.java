package org.vl4ds4m.banking.accounts.service.validation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.Errors;
import org.vl4ds4m.banking.accounts.entity.Customer;
import org.vl4ds4m.banking.accounts.util.TestEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class CustomerValidatorTest {

    private static final Customer DEFAULT = TestEntity.createDefaultCustomer();

    @DisplayName("Успешная валидация клиента")
    @Test
    void testCustomerValidation() {
        // Arrange
        var validator = new CustomerValidator();

        // Act
        var errors = validator.validateObject(DEFAULT);

        // Assert
        assertFalse(errors.hasErrors());
    }

    @DisplayName("Ошибка валидации клиента с недопустимым nickname")
    @ParameterizedTest(name = "Nickname: {0}")
    @MethodSource("provideInvalidNicknames")
    void testCustomerValidationWithInvalidNicknameFailed(String nickname, String expectedMessage) {
        // Arrange
        var customer = new Customer(
                nickname,
                DEFAULT.forename(),
                DEFAULT.surname(),
                DEFAULT.birthdate());
        var validator = new CustomerValidator();

        // Act
        var errors = validator.validateObject(customer);

        // Assert
        assertSingleFieldError(errors, CustomerValidator.NICKNAME_FIELD, nickname, expectedMessage);
    }

    @DisplayName("Ошибка валидации клиента с недопустимым именем")
    @ParameterizedTest(name = "Имя: {0}")
    @MethodSource("provideInvalidRealNames")
    void testCustomerValidationWithInvalidForenameFailed(String forename, String expectedMessage) {
        // Arrange
        var customer = new Customer(
                DEFAULT.nickname(),
                forename,
                DEFAULT.surname(),
                DEFAULT.birthdate());
        var validator = new CustomerValidator();

        // Act
        var errors = validator.validateObject(customer);

        // Assert
        assertSingleFieldError(errors, CustomerValidator.FORENAME_FIELD, forename, expectedMessage);
    }

    @DisplayName("Ошибка валидации клиента с недопустимой фамилией")
    @ParameterizedTest(name = "Фамилия: {0}")
    @MethodSource("provideInvalidRealNames")
    void testCustomerValidationWithInvalidSurnameFailed(String surname, String expectedMessage) {
        // Arrange
        var customer = new Customer(
                DEFAULT.nickname(),
                DEFAULT.forename(),
                surname,
                DEFAULT.birthdate());
        var validator = new CustomerValidator();

        // Act
        var errors = validator.validateObject(customer);

        // Assert
        assertSingleFieldError(errors, CustomerValidator.SURNAME_FIELD, surname, expectedMessage);
    }

    @DisplayName("Ошибка валидации клиента с несколькими ошибками в имени")
    @Test
    void testCustomerValidationWithSeveralErrorsInForenameFailed() {
        // Arrange
        var wrongName = "name with MANY_mistakes";
        var customer = new Customer(
                DEFAULT.nickname(),
                wrongName,
                DEFAULT.surname(),
                DEFAULT.birthdate());
        var validator = new CustomerValidator();

        // Act
        var errors = validator.validateObject(customer);

        // Assert
        assertEquals(3, errors.getErrorCount());
        assertEquals(3, errors.getFieldErrorCount());

        var fieldErrors = errors.getFieldErrors();
        fieldErrors.forEach(e -> {
            assertEquals(CustomerValidator.FORENAME_FIELD, e.getField());
            assertEquals(wrongName, e.getRejectedValue());
        });

        assertTrue(hasMessage(fieldErrors, CustomerValidator.ONLY_LETTERS_IN_REAL_NAME));
        assertTrue(hasMessage(fieldErrors, CustomerValidator.REAL_NAME_FIRST_CHAR));
        assertTrue(hasMessage(fieldErrors, CustomerValidator.REAL_NAME_SUBSEQUENT_CHARS));
    }

    @DisplayName("Ошибка валидации клиента с недопустимым возрастом")
    @ParameterizedTest(name = "День рождения: {0}")
    @MethodSource("provideInvalidBirthDates")
    void testCustomerValidationWithInvalidAgeFailed(LocalDate birthDate) {
        // Arrange
        var customer = new Customer(
                "strange_client",
                "Benjamin",
                "Button",
                birthDate);
        var validator = new CustomerValidator();

        // Act
        var errors = validator.validateObject(customer);

        // Assert
        assertSingleFieldError(errors, CustomerValidator.BIRTHDATE_FIELD, birthDate, CustomerValidator.AGE_RANGE);
    }

    private static Stream<Arguments> provideInvalidNicknames() {
        return Stream.of(
                arguments("ab", CustomerValidator.NICKNAME_LENGTH),
                arguments("this_nick_name_too_long", CustomerValidator.NICKNAME_LENGTH),
                arguments("inValid_chars", CustomerValidator.NICKNAME_RULE),
                arguments("invalid-chars", CustomerValidator.NICKNAME_RULE),
                arguments("invalid chars ", CustomerValidator.NICKNAME_RULE),
                arguments("1nvalid", CustomerValidator.NICKNAME_RULE),
                arguments("_invalid", CustomerValidator.NICKNAME_RULE),
                arguments("invalid_", CustomerValidator.NICKNAME_RULE),
                arguments("in__valid", CustomerValidator.NICKNAME_RULE),
                arguments("inv\uD801\uDCDFlid", CustomerValidator.NICKNAME_RULE),
                arguments("in-valid chars", CustomerValidator.NICKNAME_RULE));
    }

    private static Stream<Arguments> provideInvalidRealNames() {
        return Stream.of(
                arguments("", CustomerValidator.REAL_NAME_LENGTH),
                arguments("Abrakadabrasdfgsdsdfsdidpgepvideopvnsd", CustomerValidator.REAL_NAME_LENGTH),
                arguments("Inv4l1d", CustomerValidator.ONLY_LETTERS_IN_REAL_NAME),
                arguments("Invalid_chars", CustomerValidator.ONLY_LETTERS_IN_REAL_NAME),
                arguments("Invalid chars ", CustomerValidator.ONLY_LETTERS_IN_REAL_NAME),
                arguments("Inv\uD801\uDCDFlid", CustomerValidator.ONLY_LETTERS_IN_REAL_NAME),
                arguments("everylowercase", CustomerValidator.REAL_NAME_FIRST_CHAR),
                arguments("SomeUpperLetters", CustomerValidator.REAL_NAME_SUBSEQUENT_CHARS));
    }

    private static Stream<LocalDate> provideInvalidBirthDates() {
        var now = LocalDate.now();
        return Stream.of(now.minusYears(200), now.minusYears(10));
    }

    private static void assertSingleFieldError(Errors errors, String field, Object rejectedValue, String message) {
        assertEquals(1, errors.getErrorCount());
        assertEquals(1, errors.getFieldErrorCount());

        var error = errors.getFieldError();
        assertNotNull(error);
        assertEquals(field, error.getField());
        assertEquals(rejectedValue, error.getRejectedValue());
        assertEquals(message, error.getDefaultMessage());
    }

    private static boolean hasMessage(List<? extends DefaultMessageSourceResolvable> errors, String message) {
        return errors.stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .anyMatch(m -> Objects.equals(message, m));
    }
}
