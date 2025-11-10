package org.vl4ds4m.banking.accounts.service.expection;

import lombok.Getter;
import org.springframework.validation.Errors;
import org.vl4ds4m.banking.common.util.To;

public class ValidationException extends ServiceException {

    @Getter
    private final Errors errors;

    public ValidationException(Errors errors) {
        super(To.string(errors));
        this.errors = errors;
    }
}
