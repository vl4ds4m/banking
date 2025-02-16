package edu.vl4ds4m.tbank.service;

import edu.vl4ds4m.tbank.dto.Currency;
import edu.vl4ds4m.tbank.grpc.ConversionReply;
import edu.vl4ds4m.tbank.grpc.ConversionRequest;
import edu.vl4ds4m.tbank.grpc.ConverterServiceGrpc;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ConverterService {
    private static final Logger logger = LoggerFactory.getLogger(ConverterService.class);

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
                    logger.info("Send a request to convert currency");
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
