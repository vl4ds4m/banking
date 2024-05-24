package edu.tinkoff.service;

import edu.tinkoff.dto.Currency;
import edu.tinkoff.grpc.ConversionReply;
import edu.tinkoff.grpc.ConversionRequest;
import edu.tinkoff.grpc.ConverterServiceGrpc;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ConverterService {
    private static final Logger log = LoggerFactory.getLogger(ConverterService.class);

    private final Runnable conversion;
    private ConversionRequest request;
    private ConversionReply reply;

    public ConverterService(
            ConverterServiceGrpc.ConverterServiceBlockingStub grpcStub,
            CircuitBreaker circuitBreaker
    ) {
        conversion = circuitBreaker.decorateRunnable(() -> {
            log.info("Send a request to convert currency");
            reply = grpcStub.convert(request);
        });
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
