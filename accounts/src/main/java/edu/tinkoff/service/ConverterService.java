package edu.tinkoff.service;

import edu.tinkoff.dto.Currency;
import edu.tinkoff.grpc.ConversionReply;
import edu.tinkoff.grpc.ConversionRequest;
import edu.tinkoff.grpc.ConverterServiceGrpc;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ConverterService {
    private static final Logger log = LoggerFactory.getLogger(ConverterService.class);

    private final Runnable conversion;
    private ConversionRequest request;
    private ConversionReply reply;

    public ConverterService(
            ConverterServiceGrpc.ConverterServiceBlockingStub grpcStub,
            CircuitBreaker circuitBreaker,
            ObservationRegistry registry
    ) {
        conversion = circuitBreaker.decorateRunnable(() -> Observation
                .createNotStarted("conversion", registry)
                .observe(() -> {
                    log.info("Send a request to convert currency");
                    reply = grpcStub.convert(request);
                })
        );
    }

    public double convert(Currency from, Currency to, double amount) {
        request = ConversionRequest.newBuilder()
                .setFrom(from.toString())
                .setTo(to.toString())
                .setAmount(amount)
                .build();

        conversion.run();
        return reply.getAmount();
    }
}
