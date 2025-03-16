package edu.vl4ds4m.banking.converter.grpc;

import edu.vl4ds4m.banking.converter.ConverterExceptionHandler;
import edu.vl4ds4m.banking.converter.ConverterService;
import edu.vl4ds4m.banking.converter.exception.InvalidCurrencyException;
import edu.vl4ds4m.banking.converter.exception.NonPositiveAmountException;
import edu.vl4ds4m.banking.rates.RatesServiceException;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import io.micrometer.observation.annotation.Observed;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

@GrpcService
public class ConverterGrpcService extends ConverterGrpc.ConverterImplBase {
    private static final Logger logger =
        LoggerFactory.getLogger(ConverterGrpcService.class);

    private static final ConverterExceptionHandler exceptionHandler =
        new ConverterExceptionHandler(logger);

    private final ConverterService service;

    public ConverterGrpcService(ConverterService service) {
        this.service = service;
    }

    @Observed
    @Override
    public void convert(
        ConverterGrpcRequest request,
        StreamObserver<ConverterGrpcResponse> observer
    ) {
        logger.debug("Accept a request to convert currency");
        BigDecimal converted;
        try {
            try {
                converted = service.convert(
                    request.getFrom(),
                    request.getTo(),
                    BigDecimal.valueOf(request.getAmount()));
            } catch (RuntimeException e) {
                Status status = handleException(e);
                observer.onError(status.asRuntimeException());
                return;
            }
            ConverterGrpcResponse response = ConverterGrpcResponse.newBuilder()
                .setCurrency(request.getTo())
                .setAmount(converted.doubleValue())
                .build();
            observer.onNext(response);
            observer.onCompleted();
        } catch (RuntimeException e) {
            exceptionHandler.errorUnhandledException(e);
        }
    }

    private Status handleException(RuntimeException exception) {
        Status status;
        switch (exception) {
            case NonPositiveAmountException e -> {
                exceptionHandler.debugNonPositiveAmount(e);
                status = Status.INVALID_ARGUMENT.withDescription(e.getMessage());
            }
            case InvalidCurrencyException e -> {
                exceptionHandler.debugInvalidCurrency(e);
                status = Status.INVALID_ARGUMENT.withDescription(e.getMessage());
            }
            case RatesServiceException e -> {
                String description = exceptionHandler.warnRatesServiceError(e);
                status = Status.UNAVAILABLE.withDescription(description);
            }
            default -> {
                String description = exceptionHandler.errorUnhandledException(exception);
                status = Status.INTERNAL.withDescription(description);
            }
        }
        return status;
    }
}
