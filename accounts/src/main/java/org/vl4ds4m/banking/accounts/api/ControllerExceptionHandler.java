package org.vl4ds4m.banking.accounts.api;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.grpc.Status;
import io.grpc.StatusException;
import io.grpc.StatusRuntimeException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.vl4ds4m.banking.accounts.api.model.InvalidQueryResponse;
import org.vl4ds4m.banking.accounts.exception.InvalidDataException;
import org.vl4ds4m.banking.common.exception.ServiceException;
import org.vl4ds4m.banking.common.util.To;

@RestControllerAdvice
@Slf4j
public class ControllerExceptionHandler {

    @ExceptionHandler({
            MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class,
            MethodArgumentNotValidException.class,
            ServiceException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public InvalidQueryResponse handleInvalidQuery(Exception e) {
        String message;
        if (e instanceof MethodArgumentNotValidException target) {
            message = To.string(target.getBindingResult());
        } else {
            message = e.getMessage();
        }

        log.info("""
                Invalid query handled:
                    exception = {},
                    response message = {}""",
                e.getClass().getName(),
                message);

        return new InvalidQueryResponse(message);
    }

    @ExceptionHandler({
        ConstraintViolationException.class,
        InvalidDataException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleInvalidDataException(Exception e) {
        String message = e.getMessage();
        log.info("Handle InvalidDataException: {}", message);
        return message;
    }

    @ExceptionHandler({StatusException.class, StatusRuntimeException.class})
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public void handleGrpcException(Exception e) {
        String message = String.format(
            "Grpc exception was thrown, code: %s, description: %s",
            Status.fromThrowable(e).getCode(),
            Status.fromThrowable(e).getDescription());
        handleUnavailableConverterException(message);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public void handleUnavailableConverterException(CallNotPermittedException e) {
        handleUnavailableConverterException(e.getMessage());
    }

    private void handleUnavailableConverterException(String message) {
        log.warn("Converter is unavailable: {}", message);
    }
}
