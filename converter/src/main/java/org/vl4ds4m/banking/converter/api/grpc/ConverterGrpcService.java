package org.vl4ds4m.banking.converter.api.grpc;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.vl4ds4m.banking.common.entity.Money;
import org.vl4ds4m.banking.common.exception.ServiceException;
import org.vl4ds4m.banking.common.grpc.Currency;
import org.vl4ds4m.banking.common.util.To;
import org.vl4ds4m.banking.converter.api.ConverterExceptionHandler;
import org.vl4ds4m.banking.converter.grpc.ConvertRequest;
import org.vl4ds4m.banking.converter.grpc.ConvertResponse;
import org.vl4ds4m.banking.converter.grpc.ConverterGrpc;
import org.vl4ds4m.banking.converter.service.ConverterService;
import org.vl4ds4m.banking.converter.service.exception.RatesServiceException;

import java.math.BigDecimal;

@GrpcService
@Slf4j
@RequiredArgsConstructor
public class ConverterGrpcService extends ConverterGrpc.ConverterImplBase {

    private static final ConverterExceptionHandler exceptionHandler = new ConverterExceptionHandler(log);

    private final ConverterService service;

    @Observed
    @Override
    public void convert(
        ConvertRequest request,
        StreamObserver<ConvertResponse> observer
    ) {
        log.info("Accept a request to convert currency");
        Money converted;
        try {
            var amount = BigDecimal.valueOf(request.getAmount());
            converted = service.convert(
                toEntity(request.getFrom()),
                toEntity(request.getTo()),
                To.moneyOrReject(amount, "Amount"));
        } catch (RuntimeException e) {
            Status status = handleException(e);
            observer.onError(status.asRuntimeException());
            return;
        }
        ConvertResponse response = ConvertResponse.newBuilder()
            .setAmount(converted.amount().doubleValue())
            .build();
        observer.onNext(response);
        observer.onCompleted();
    }

    private static org.vl4ds4m.banking.common.entity.Currency toEntity(Currency currency) {
        return org.vl4ds4m.banking.common.entity.Currency.valueOf(currency.name());
    }

    private Status handleException(RuntimeException exception) {
        Status status;
        switch (exception) {
            case ServiceException e -> status = Status.INVALID_ARGUMENT.withDescription(e.getMessage());
            case RatesServiceException e -> {
                String description = exceptionHandler.warnRatesServiceError(e);
                status = Status.UNAVAILABLE.withDescription(description);
            }
            default -> throw exception;
        }
        return status;
    }
}
