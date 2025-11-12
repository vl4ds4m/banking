package org.vl4ds4m.banking.accounts.deprecation.converter;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vl4ds4m.banking.common.grpc.Currency;
import org.vl4ds4m.banking.converter.grpc.ConvertRequest;
import org.vl4ds4m.banking.converter.grpc.ConvertResponse;
import org.vl4ds4m.banking.converter.grpc.ConverterGrpc;

import java.math.BigDecimal;

public class ConverterService {
    private static final Logger logger = LoggerFactory.getLogger(ConverterService.class);

    private final Runnable conversion;
    private ConvertRequest request;
    private ConvertResponse reply;

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
        request = ConvertRequest.newBuilder()
            .setFrom(from)
            .setTo(to)
            .setAmount(amount.doubleValue())
            .build();
        conversion.run();
        return BigDecimal.valueOf(reply.getAmount());//Conversions.setScale(reply.getAmount());
    }
}
