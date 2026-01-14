package org.vl4ds4m.banking.common.handler.exception;

import io.grpc.Status;
import io.grpc.StatusException;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.grpc.server.exception.GrpcExceptionHandler;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
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
            case OAuth2AuthorizationException e -> {
                return handleOAuth2Error(e);
            }
            case OAuth2AuthenticationException e -> {
                return handleOAuth2Error(e);
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

    private StatusException handleOAuth2Error(OAuth2AuthorizationException e) {
        exceptionLogger.logServiceError("auth", e);
        return Status.UNAVAILABLE
                .withDescription(e.getError().toString())
                .asException();
    }

    private StatusException handleOAuth2Error(OAuth2AuthenticationException e) {
        exceptionLogger.logServiceError("auth", e);
        return Status.UNAVAILABLE
                .withDescription(e.getError().toString())
                .asException();
    }

}
