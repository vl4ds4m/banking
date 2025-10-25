package org.vl4ds4m.banking.converter.api.grpc;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.vl4ds4m.banking.converter.api.ConverterExceptionHandler;
import org.vl4ds4m.banking.converter.grpc.ConverterGrpc;
import org.vl4ds4m.banking.converter.grpc.ConverterGrpcRequest;
import org.vl4ds4m.banking.converter.grpc.ConverterGrpcResponse;
import org.vl4ds4m.banking.converter.service.ConverterService;
import org.vl4ds4m.banking.converter.api.model.Currency;
import org.vl4ds4m.banking.converter.service.exception.InvalidCurrencyException;
import org.vl4ds4m.banking.converter.service.exception.NonPositiveAmountException;
import org.vl4ds4m.banking.converter.service.exception.RatesServiceException;

import java.math.BigDecimal;

@GrpcService
@Slf4j
@RequiredArgsConstructor
public class ConverterGrpcService extends ConverterGrpc.ConverterImplBase {

    private static final ConverterExceptionHandler exceptionHandler =
        new ConverterExceptionHandler(log);

    private final ConverterService service;

    @Observed
    @Override
    public void convert(
        ConverterGrpcRequest request,
        StreamObserver<ConverterGrpcResponse> observer
    ) {
        log.info("Accept a request to convert currency");
        BigDecimal converted;
        try {
            converted = service.convert(
                Currency.fromValue(request.getFrom()),
                Currency.fromValue(request.getTo()),
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
            default -> throw exception;
        }
        return status;
    }
}
