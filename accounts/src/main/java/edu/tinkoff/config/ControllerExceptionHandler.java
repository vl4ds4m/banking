package edu.tinkoff.config;

import io.grpc.Status;
import io.grpc.StatusException;
import io.grpc.StatusRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(ControllerExceptionHandler.class);

    @ExceptionHandler({StatusException.class, StatusRuntimeException.class})
    public ResponseEntity<?> handleGrpcException(Exception e) {
        logger.error(
                "Grpc exception was thrown, code: {}, description: {}",
                Status.fromThrowable(e).getCode(),
                Status.fromThrowable(e).getDescription()
        );
        return ResponseEntity.internalServerError().build();
    }

    @ExceptionHandler
    public ResponseEntity<?> handleException(Exception e) {
        logger.error("Exception was thrown: {}", e.getMessage());
        return ResponseEntity.internalServerError().build();
    }
}
