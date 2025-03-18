package edu.vl4ds4m.banking;

import edu.vl4ds4m.banking.exception.InvalidDataException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.grpc.Status;
import io.grpc.StatusException;
import io.grpc.StatusRuntimeException;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class ControllerExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(ControllerExceptionHandler.class);

    @ExceptionHandler({
        ConstraintViolationException.class,
        MethodArgumentTypeMismatchException.class,
        InvalidDataException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleInvalidDataException(RuntimeException e) {
        String message = e.getMessage();
        logger.debug("Handle InvalidDataException: {}", message);
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
        logger.warn("Converter is unavailable: {}", message);
    }
}
