package org.vl4ds4m.banking.common.exception;

import lombok.Getter;

public class ServiceException extends RuntimeException {

    @Getter
    private final String service;

    public ServiceException(String service, Throwable cause) {
        super(buildMessage(service, cause), cause);
        this.service = service;
    }

    private static String buildMessage(String service, Throwable cause) {
        return "Error in %s service: %s".formatted(service, cause);
    }
}
