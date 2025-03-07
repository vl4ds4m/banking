package edu.vl4ds4m.banking.controller;

import edu.vl4ds4m.banking.exception.ConverterExceptionLogger;
import edu.vl4ds4m.banking.exception.InvalidCurrencyException;
import edu.vl4ds4m.banking.exception.NonPositiveAmountException;
import edu.vl4ds4m.banking.exception.RatesServiceException;
import edu.vl4ds4m.banking.service.ConverterService;
import edu.vl4ds4m.banking.grpc.ConverterGrpc;
import edu.vl4ds4m.banking.grpc.ConverterGrpcRequest;
import edu.vl4ds4m.banking.grpc.ConverterGrpcResponse;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import io.micrometer.observation.annotation.Observed;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

@GrpcService
public class ConverterGrpcController extends ConverterGrpc.ConverterImplBase {
    private static final Logger logger = LoggerFactory.getLogger(ConverterGrpcController.class);
    private static final ConverterExceptionLogger exceptionLogger = new ConverterExceptionLogger(logger);

    private final ConverterService service;

    public ConverterGrpcController(ConverterService service) {
        this.service = service;
    }

    @Observed
    @Override
    public void convert(ConverterGrpcRequest request, StreamObserver<ConverterGrpcResponse> observer) {
        logger.debug("Accept a request to convert currency");
        try {
            BigDecimal converted = service.convert(
                    request.getFrom(),
                    request.getTo(),
                    BigDecimal.valueOf(request.getAmount())
            );
            ConverterGrpcResponse response = ConverterGrpcResponse.newBuilder()
                    .setCurrency(request.getTo())
                    .setAmount(converted.doubleValue())
                    .build();
            observer.onNext(response);
            observer.onCompleted();
        } catch (RuntimeException e) {
            handleException(e, observer);
        }
    }

    private void handleException(RuntimeException exception, StreamObserver<ConverterGrpcResponse> observer) {
        Status status;
        try {
            throw exception;
        } catch (NumberFormatException | NonPositiveAmountException e) {
            exceptionLogger.debugNonPositiveAmountMessage();
            status = Status.INVALID_ARGUMENT
                    .withDescription("Amount is non-positive");
        } catch (InvalidCurrencyException e) {
            exceptionLogger.debugInvalidCurrencyMessage(e.currency);
            String message = String.format("Currency %s is invalid", e.currency);
            status = Status.INVALID_ARGUMENT.withDescription(message);
        } catch (RatesServiceException e) {
            String message = e.getMessage();
            exceptionLogger.warnRatesServiceError(message);
            status = Status.UNAVAILABLE
                    .withDescription("Exception is caused by RatesService: " + message);
        } catch (RuntimeException e) {
            logger.error("Unhandled exception", exception);
            status = Status.INTERNAL.withCause(exception);
        }
        observer.onError(status.asRuntimeException());
    }
}
