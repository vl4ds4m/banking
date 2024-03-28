package edu.tinkoff.service;

import edu.tinkoff.dto.Currency;
import edu.tinkoff.grpc.ConversionReply;
import edu.tinkoff.grpc.ConversionRequest;
import edu.tinkoff.grpc.ConverterServiceGrpc.ConverterServiceBlockingStub;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ConverterService {
    private final Runnable conversion;
    private ConversionRequest request;
    private ConversionReply reply;

    public ConverterService(
            ConverterServiceBlockingStub grpcStub,
            CircuitBreaker circuitBreaker
    ) {
        conversion = circuitBreaker.decorateRunnable(
                () -> reply = grpcStub.convert(request)
        );
    }

    public BigDecimal convert(Currency from, Currency to, BigDecimal amount) {
        request = ConversionRequest.newBuilder()
                .setFrom(from.toString())
                .setTo(to.toString())
                .setAmount(amount.doubleValue())
                .build();

        conversion.run();
        return BigDecimal.valueOf(reply.getAmount());
    }
}
