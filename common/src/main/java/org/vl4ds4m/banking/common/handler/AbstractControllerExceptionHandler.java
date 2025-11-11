package org.vl4ds4m.banking.common.handler;

import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.vl4ds4m.banking.common.exception.ServiceException;
import org.vl4ds4m.banking.common.util.To;

public abstract class AbstractControllerExceptionHandler {

    protected abstract Logger log();

    protected abstract Object buildResponse(String message);

    @ExceptionHandler({
            MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class,
            MethodArgumentNotValidException.class,
            ServiceException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Object handleInvalidQuery(Exception e) {
        String message;
        if (e instanceof MethodArgumentNotValidException target) {
            message = To.string(target.getBindingResult());
        } else {
            message = e.getMessage();
        }

        log().info("""
                Invalid query handled:
                    exception = {},
                    response message = {}""",
                e.getClass().getName(),
                message);

        return buildResponse(message);
    }

    // TODO
    // @ExceptionHandler({StatusException.class, StatusRuntimeException.class})
    // @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    // public void handleGrpcException(Exception e) {
    //     String message = String.format(
    //         "Grpc exception was thrown, code: %s, description: %s",
    //         Status.fromThrowable(e).getCode(),
    //         Status.fromThrowable(e).getDescription());
    //     handleUnavailableConverterException(message);
    // }
    //
    // @ExceptionHandler
    // @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    // public void handleUnavailableConverterException(CallNotPermittedException e) {
    //     handleUnavailableConverterException(e.getMessage());
    // }
    //
    // private void handleUnavailableConverterException(String message) {
    //     log.warn("Converter is unavailable: {}", message);
    // }
}
