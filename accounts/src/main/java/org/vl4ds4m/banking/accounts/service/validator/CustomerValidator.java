package org.vl4ds4m.banking.accounts.service.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.vl4ds4m.banking.accounts.entity.Customer;

import java.time.LocalDate;
import java.util.regex.Pattern;

@Component
public class CustomerValidator implements Validator {

    private static final int MIN_LOGIN_LENGTH = 3;
    private static final int MAX_LOGIN_LENGTH = 20;

    private static final Pattern LOGIN_PATTERN = Pattern.compile("[a-z][a-z0-9_]+[a-z0-9]");

    private static final int MAX_REAL_NAME_LENGTH = 30;

    public static final String LOGIN_FIELD = "login";
    public static final String FORENAME_FIELD = "forename";
    public static final String SURNAME_FIELD = "surname";
    public static final String BIRTHDATE_FIELD = "birthdate";

    public static final String LOGIN_LENGTH = "Login length must be between %d and %d"
            .formatted(MIN_LOGIN_LENGTH, MAX_LOGIN_LENGTH);

    public static final String LOGIN_RULE = "Login must contains only " +
            "lowercase latin letters [a-z], digits and '_'. " +
            "First character must be a letter " +
            "and last character must not be '_'. " +
            "Two '_' must not be adjacent.";

    public static final String REAL_NAME_LENGTH = "Real name length must be between 1 and %d."
            .formatted(MAX_REAL_NAME_LENGTH);

    public static final String ONLY_LETTERS_IN_REAL_NAME = "Real name must contains only letters.";

    public static final String REAL_NAME_FIRST_CHAR = "First character in real name must be uppercase.";

    public static final String REAL_NAME_SUBSEQUENT_CHARS = "Subsequent characters in real name must be lowercase.";

    public static final String AGE_RANGE = "According to birthdate, age must be between 14 and 120 years old.";

    @Override
    public boolean supports(Class<?> clazz) {
        return Customer.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        var customer = (Customer) target;

        validateLogin(customer.login(), errors);

        validateRealName(FORENAME_FIELD, customer.forename(), errors);
        validateRealName(SURNAME_FIELD, customer.surname(), errors);

        validateBirthdate(customer.birthdate(), errors);
    }

    private void validateLogin(String value, Errors errors) {
        if (value.length() > MAX_LOGIN_LENGTH || value.length() < 3) {
            rejectOnInvalidLength(errors, LOGIN_FIELD, LOGIN_LENGTH);
            return;
        }

        boolean match = LOGIN_PATTERN.matcher(value).matches();

        if (match) {
            for (int i = 1; i < value.length(); i++) {
                if (value.charAt(i - 1) == '_' && value.charAt(i) == '_') {
                    match = false;
                    break;
                }
            }
        }

        if (!match) {
            rejectOnInvalidContent(errors, LOGIN_FIELD, LOGIN_RULE);
        }
    }

    private void validateRealName(String field, String value, Errors errors) {
        if (value.length() > MAX_REAL_NAME_LENGTH || value.isEmpty()) {
            rejectOnInvalidLength(errors, field, REAL_NAME_LENGTH);
            return;
        }

        int[] codePoints = value.codePoints().toArray();
        boolean hasInvalidChar = false;
        boolean hasInvalidSubsequentChar = false;

        for (int i = 0; i < codePoints.length; i++) {
            if (hasInvalidChar && hasInvalidSubsequentChar) {
                break;
            }

            if (!isRealNameLetter(codePoints[i])) {
                if (!hasInvalidChar) {
                    rejectOnInvalidContent(errors, field, ONLY_LETTERS_IN_REAL_NAME);
                    hasInvalidChar = true;
                }
                continue;
            }

            char ch = (char) codePoints[i];

            if (i == 0) {
                if (!Character.isUpperCase(ch)) {
                    rejectOnInvalidContent(errors, field, REAL_NAME_FIRST_CHAR);
                }
                continue;
            }

            if (!hasInvalidSubsequentChar && !Character.isLowerCase(ch)) {
                rejectOnInvalidContent(errors, field, REAL_NAME_SUBSEQUENT_CHARS);
                hasInvalidSubsequentChar = true;
            }
        }
    }

    private void validateBirthdate(LocalDate birthDate, Errors errors) {
        var now = LocalDate.now();
        var maxBirthDate = now.minusYears(14);
        var minBirthDate = now.minusYears(121).plusDays(1);

        if (birthDate.isAfter(maxBirthDate) || birthDate.isBefore(minBirthDate)) {
            errors.rejectValue(BIRTHDATE_FIELD, "age", AGE_RANGE);
        }
    }

    private static boolean isRealNameLetter(int codePoint) {
        return !Character.isSupplementaryCodePoint(codePoint)
                && (Character.isLowerCase(codePoint) || Character.isUpperCase(codePoint));
    }

    private static void rejectOnInvalidLength(Errors errors, String field, String message) {
        errors.rejectValue(field, "length", message);
    }

    private static void rejectOnInvalidContent(Errors errors, String field, String message) {
        errors.rejectValue(field, "content", message);
    }
}
