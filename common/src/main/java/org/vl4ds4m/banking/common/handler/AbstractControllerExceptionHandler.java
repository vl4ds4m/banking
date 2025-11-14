package org.vl4ds4m.banking.common.handler;

import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.vl4ds4m.banking.common.exception.InvalidQueryException;
import org.vl4ds4m.banking.common.exception.ServiceException;
import org.vl4ds4m.banking.common.util.To;

public abstract class AbstractControllerExceptionHandler {

    protected final ExceptionLogger exceptionLogger = new ExceptionLogger(this::log);

    protected abstract Logger log();

    protected abstract Object buildResponse(String message);

    @ExceptionHandler({
            MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class,
            InvalidQueryException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Object handleInvalidQuery(Exception e) {
        exceptionLogger.logInvalidQuery(e);
        return buildResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Object handleInvalidQuery(MethodArgumentNotValidException e) {
        var message = To.string(e.getBindingResult());
        exceptionLogger.logInvalidQuery(e, message);
        return buildResponse(message);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public Object handleServiceError(ServiceException e) {
        exceptionLogger.logServiceError(e);
        return buildResponse(e.getMessage());
    }
}
