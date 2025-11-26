package org.vl4ds4m.banking.common.handler.exception;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;
import org.vl4ds4m.banking.common.exception.InvalidQueryException;
import org.vl4ds4m.banking.common.exception.ServiceException;

@GrpcAdvice
@Slf4j
public class GrpcServiceExceptionHandler {

    private final ExceptionLogger exceptionLogger = new ExceptionLogger(log);

    @GrpcExceptionHandler
    public StatusRuntimeException handleInvalidQuery(InvalidQueryException e) {
        exceptionLogger.logInvalidQuery(e);
        return Status.INVALID_ARGUMENT
                .withDescription(e.getMessage())
                .asRuntimeException();
    }

    @GrpcExceptionHandler
    public StatusRuntimeException handleServiceError(ServiceException e) {
        exceptionLogger.logServiceError(e);
        return Status.UNAVAILABLE
                .withDescription(e.getMessage())
                .asRuntimeException();
    }
}
