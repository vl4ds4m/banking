package edu.vl4ds4m.banking.converter;

import edu.vl4ds4m.banking.Conversions;
import edu.vl4ds4m.banking.dto.Currency;
import edu.vl4ds4m.banking.grpc.ConverterGrpcResponse;
import edu.vl4ds4m.banking.grpc.ConverterGrpcRequest;
import edu.vl4ds4m.banking.grpc.ConverterGrpc;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
