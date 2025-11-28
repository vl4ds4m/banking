package org.vl4ds4m.banking.common.exception;

import lombok.Getter;

public class ServiceException extends RuntimeException {

    @Getter
    private final String service;

    public ServiceException(String service, Throwable cause) {
        super(buildMessage(service, cause), cause);
        this.service = service;
    }

    public ServiceException(String service, String message) {
        super(buildMessage(service, message));
        this.service = service;
    }

    private static String buildMessage(String service, Object info) {
        return "Error in %s service: %s".formatted(service, info);
    }
}
