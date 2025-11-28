package org.vl4ds4m.banking.common.handler.exception;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.vl4ds4m.banking.common.exception.ServiceException;

@RequiredArgsConstructor
public class ExceptionLogger {

    private final Logger log;

    public void logInvalidQuery(Exception exception) {
        logInvalidQuery(exception, exception.getMessage());
    }

    public void logInvalidQuery(Exception exception, String message) {
        log.info("""
                Invalid query handled:
                    exception = {},
                    response message = {}""",
                exception.getClass().getName(),
                message);
    }

    public void logServiceError(ServiceException exception) {
        var cause = exception.getCause();
        if (cause == null) {
            logServiceError(exception.getService(), exception.getMessage());
        } else {
            logServiceError(exception.getService(), cause);
        }
    }

    public void logServiceError(String service, Throwable cause) {
        log.warn("""
                Service error handled:
                    service = {}
                    cause = {}
                    message = {}""",
                service,
                cause.getClass().getName(),
                cause.getMessage());
    }

    public void logServiceError(String service, String message) {
        log.warn("""
                Service error handled:
                    service = {}
                    message = {}""",
                service,
                message);
    }
}
