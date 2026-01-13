package org.vl4ds4m.banking.common.handler.exception;

import io.grpc.Status;
import io.grpc.StatusException;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.grpc.server.exception.GrpcExceptionHandler;
import org.springframework.stereotype.Component;
import org.vl4ds4m.banking.common.exception.InvalidQueryException;
import org.vl4ds4m.banking.common.exception.ServiceException;

@Component
@Slf4j
public class GrpcServiceExceptionHandler implements GrpcExceptionHandler {

    private final ExceptionLogger exceptionLogger = new ExceptionLogger(log);

    @Override
    public @Nullable StatusException handleException(Throwable exception) {
        switch (exception) {
            case InvalidQueryException e -> {
                return handleInvalidQuery(e);
            }
            case ServiceException e -> {
                return handleServiceError(e);
            }
            default -> {
                return null;
            }
        }
    }

    private StatusException handleInvalidQuery(InvalidQueryException e) {
        exceptionLogger.logInvalidQuery(e);
        return Status.INVALID_ARGUMENT
                .withDescription(e.getMessage())
                .asException();
    }

    private StatusException handleServiceError(ServiceException e) {
        exceptionLogger.logServiceError(e);
        return Status.UNAVAILABLE
                .withDescription(e.getMessage())
                .asException();
    }

}
