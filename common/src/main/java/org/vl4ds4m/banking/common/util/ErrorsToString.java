package org.vl4ds4m.banking.common.util;

import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.List;

class ErrorsToString {

    private ErrorsToString() {}

    static String string(Errors errors) {
        var sb = new StringBuilder();

        sb.append("Validation errors: object = ")
                .append(errors.getObjectName())
                .append(", common messages = [");
        appendErrorMessages(sb, errors.getGlobalErrors());
        sb.append("], field errors = [");
        appendFieldErrors(sb, errors);
        sb.append("]");

        return sb.toString();
    }

    private static void appendFieldErrors(StringBuilder sb, Errors errors) {
        var fields = errors.getFieldErrors()
                .stream()
                .map(FieldError::getField)
                .distinct()
                .toList();
        for (int i = 0; i < fields.size(); i++) {
            if (i > 0) {
                sb.append("; ");
            }
            var field = fields.get(i);
            appendFieldErrors(sb, errors.getFieldErrors(field));
        }
    }

    private static void appendFieldErrors(StringBuilder sb, List<FieldError> errors) {
        var field = errors.getFirst().getField();
        var value = errors.getFirst().getRejectedValue();
        sb.append("field = ").append(field)
                .append(", value = '").append(value)
                .append("', messages = [");
        appendErrorMessages(sb, errors);
        sb.append("]");
    }

    private static void appendErrorMessages(StringBuilder sb, List<? extends ObjectError> errors) {
        for (int i = 0; i < errors.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append("'")
                    .append(errors.get(i).getDefaultMessage())
                    .append("'");
        }
    }
}
