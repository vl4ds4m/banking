package edu.vl4ds4m.tbank.controller;

import edu.vl4ds4m.tbank.exception.InvalidDataException;
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

@RestControllerAdvice
public class ControllerExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(ControllerExceptionHandler.class);

    @ExceptionHandler({ConstraintViolationException.class, InvalidDataException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleInvalidDataException(RuntimeException e) {
        logger.debug("Handle bad request");
        return e.getMessage();
    }

    @ExceptionHandler({StatusException.class, StatusRuntimeException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public void handleGrpcException(Exception e) {
        logger.error(
                "Grpc exception was thrown, code: {}, description: {}",
                Status.fromThrowable(e).getCode(),
                Status.fromThrowable(e).getDescription()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public void handleException(Exception e) {
        logger.error(e.getMessage());
    }
}
