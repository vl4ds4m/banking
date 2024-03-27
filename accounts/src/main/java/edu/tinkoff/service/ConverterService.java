package edu.tinkoff.service;

import edu.tinkoff.dto.Currency;
import edu.tinkoff.grpc.ConversionReply;
import edu.tinkoff.grpc.ConversionRequest;
import edu.tinkoff.grpc.ConverterServiceGrpc.ConverterServiceBlockingStub;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ConverterService {
    private final ConverterServiceBlockingStub grpcStub;

    public ConverterService(ConverterServiceBlockingStub grpcStub) {
        this.grpcStub = grpcStub;
    }

    public BigDecimal convert(Currency from, Currency to, BigDecimal amount) {
        ConversionRequest request = ConversionRequest.newBuilder()
                .setFrom(from.toString())
                .setTo(to.toString())
                .setAmount(amount.doubleValue())
                .build();
        ConversionReply reply = grpcStub.convert(request);
        return BigDecimal.valueOf(reply.getAmount());
    }
}
