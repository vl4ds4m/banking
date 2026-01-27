package org.vl4ds4m.banking.common.exception;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.util.Set;

public class ServiceException extends RuntimeException {

    private static final Set<Status.Code> GRPC_AVAILABILITY_ERRORS = Set.of(
            Status.Code.UNAVAILABLE,
            Status.Code.DATA_LOSS);

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

    public static HttpStatusCode asHttpStatus(ServiceException e) {
        Throwable cause = e.getCause();
        if (isServiceUnavailable(cause)) {
            return HttpStatus.SERVICE_UNAVAILABLE;
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public static Status asGrpcStatus(ServiceException e) {
        Throwable cause = e.getCause();
        if (isServiceUnavailable(cause)) {
            return Status.UNAVAILABLE;
        }
        return Status.INTERNAL;
    }

    private static String buildMessage(String service, Object info) {
        return "Error in %s service: %s".formatted(service, info);
    }

    private static boolean isServiceUnavailable(Throwable cause) {
        switch (cause) {
            case RestClientResponseException e -> {
                return e.getStatusCode().isSameCodeAs(HttpStatus.SERVICE_UNAVAILABLE);
            }
            case RestClientException _ -> {
                return true;
            }
            case StatusRuntimeException e -> {
                Status.Code code = e.getStatus().getCode();
                return GRPC_AVAILABILITY_ERRORS.contains(code);
            }
            default -> {
                return false;
            }
        }
    }
}
