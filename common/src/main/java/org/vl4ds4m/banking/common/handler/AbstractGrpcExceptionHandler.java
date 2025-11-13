package org.vl4ds4m.banking.common.handler;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;
import org.slf4j.Logger;
import org.vl4ds4m.banking.common.exception.InvalidQueryException;
import org.vl4ds4m.banking.common.exception.ServiceException;

public abstract class AbstractGrpcExceptionHandler {

    protected final ExceptionLogger exceptionLogger = new ExceptionLogger(this::log);

    protected abstract Logger log();

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

    @GrpcExceptionHandler
    public StatusRuntimeException handleCriticalError(Throwable e) {
        exceptionLogger.logCriticalError(e);
        return Status.INTERNAL
                .withDescription(e.toString())
                .asRuntimeException();
    }
}
