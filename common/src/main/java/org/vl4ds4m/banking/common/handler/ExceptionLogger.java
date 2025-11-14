package org.vl4ds4m.banking.common.handler;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.vl4ds4m.banking.common.exception.ServiceException;

import java.util.function.Supplier;

@RequiredArgsConstructor
public class ExceptionLogger {

    private final Supplier<Logger> log;

    public void logInvalidQuery(Exception exception) {
        logInvalidQuery(exception, exception.getMessage());
    }

    public void logInvalidQuery(Exception exception, String message) {
        log.get().info("""
                Invalid query handled:
                    exception = {},
                    response message = {}""",
                exception.getClass().getName(),
                message);
    }

    public void logServiceError(ServiceException exception) {
        logServiceError(exception.getService(), exception.getCause());
    }

    public void logServiceError(String service, Throwable cause) {
        log.get().warn("""
                Service error handled:
                    service = {}
                    cause = {}
                    message = {}""",
                service,
                cause.getClass().getName(),
                cause.getMessage());
    }
}
