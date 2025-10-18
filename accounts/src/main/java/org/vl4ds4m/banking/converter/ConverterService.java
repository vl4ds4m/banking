package org.vl4ds4m.banking.converter;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vl4ds4m.banking.Conversions;
import org.vl4ds4m.banking.converter.grpc.ConverterGrpc;
import org.vl4ds4m.banking.converter.grpc.ConverterGrpcRequest;
import org.vl4ds4m.banking.converter.grpc.ConverterGrpcResponse;
import org.vl4ds4m.banking.currency.Currency;

import java.math.BigDecimal;

public class ConverterService {
    private static final Logger logger = LoggerFactory.getLogger(ConverterService.class);

    private final Runnable conversion;
    private ConverterGrpcRequest request;
    private ConverterGrpcResponse reply;

    public ConverterService(
        ConverterGrpc.ConverterBlockingStub grpcStub,
        CircuitBreaker circuitBreaker,
        ObservationRegistry registry
    ) {
        conversion = circuitBreaker.decorateRunnable(() -> Observation
            .createNotStarted("conversion", registry)
            .observe(() -> {
                logger.debug("Send ConverterRequest[from={}, to={}, amount={}]",
                    request.getFrom(), request.getTo(), request.getAmount());
                reply = grpcStub.convert(request);
            })
        );
    }

    public BigDecimal convert(Currency from, Currency to, BigDecimal amount) {
        request = ConverterGrpcRequest.newBuilder()
            .setFrom(from.toString())
            .setTo(to.toString())
            .setAmount(amount.doubleValue())
            .build();
        conversion.run();
        return Conversions.setScale(reply.getAmount());
    }
}
