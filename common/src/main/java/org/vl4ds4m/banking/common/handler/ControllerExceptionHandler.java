package org.vl4ds4m.banking.common.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.vl4ds4m.banking.common.exception.InvalidQueryException;
import org.vl4ds4m.banking.common.exception.ServiceException;
import org.vl4ds4m.banking.common.openapi.model.ErrorMessageResponse;
import org.vl4ds4m.banking.common.util.To;

@RestControllerAdvice
@Slf4j
public class ControllerExceptionHandler {

    private final ExceptionLogger exceptionLogger = new ExceptionLogger(log);

    @ExceptionHandler({
            MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class,
            InvalidQueryException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessageResponse handleInvalidQuery(Exception e) {
        exceptionLogger.logInvalidQuery(e);
        return buildResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessageResponse handleInvalidQuery(MethodArgumentNotValidException e) {
        var message = To.string(e.getBindingResult());
        exceptionLogger.logInvalidQuery(e, message);
        return buildResponse(message);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ErrorMessageResponse handleServiceError(ServiceException e) {
        exceptionLogger.logServiceError(e);
        return buildResponse(e.getMessage());
    }

    private static ErrorMessageResponse buildResponse(String message) {
        return new ErrorMessageResponse(message);
    }
}
