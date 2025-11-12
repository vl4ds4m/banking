package org.vl4ds4m.banking.converter.http.api;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.vl4ds4m.banking.common.handler.AbstractControllerExceptionHandler;
import org.vl4ds4m.banking.converter.http.api.model.InvalidQueryResponse;

@RestControllerAdvice
@Slf4j
public class ControllerExceptionHandler extends AbstractControllerExceptionHandler {

    @Override
    protected Logger log() {
        return log;
    }

    @Override
    protected InvalidQueryResponse buildResponse(String message) {
        return new InvalidQueryResponse(message);
    }
}
