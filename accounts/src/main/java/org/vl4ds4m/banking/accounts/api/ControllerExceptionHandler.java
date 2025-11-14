package org.vl4ds4m.banking.accounts.api;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.vl4ds4m.banking.accounts.api.http.model.ErrorMessageResponse;
import org.vl4ds4m.banking.common.handler.AbstractControllerExceptionHandler;

@RestControllerAdvice
@Slf4j
public class ControllerExceptionHandler extends AbstractControllerExceptionHandler {

    @Override
    protected Logger log() {
        return log;
    }

    @Override
    protected ErrorMessageResponse buildResponse(String message) {
        return new ErrorMessageResponse(message);
    }
}
