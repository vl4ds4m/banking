package edu.tinkoff.service;

import edu.tinkoff.dto.CurrencyMessage;
import edu.tinkoff.dto.Currency;
import edu.tinkoff.grpc.ConversionReply;
import edu.tinkoff.grpc.ConversionRequest;
import edu.tinkoff.grpc.ConverterServiceGrpc.ConverterServiceBlockingStub;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Objects;

@Service
public class ConverterService {
    private final RestTemplate restTemplate;
    private final ConverterServiceBlockingStub grpcStub;

    private String converterUrl;

    public ConverterService(
            @Qualifier("auth") RestTemplate restTemplate,
            ConverterServiceBlockingStub grpcStub
    ) {
        this.restTemplate = restTemplate;
        this.grpcStub = grpcStub;
    }

    @Value("${services.converter.url}")
    public void setConverterUrl(String converterUrl) {
        this.converterUrl = converterUrl;
    }

    public BigDecimal convert(Currency from, Currency to, BigDecimal amount) {
        if (converterUrl.startsWith("http")) {
            CurrencyMessage message = restTemplate.getForObject(
                    converterUrl,
                    CurrencyMessage.class,
                    from, to, amount
            );
            return Objects.requireNonNull(message).amount();
        }

        ConversionRequest request = ConversionRequest.newBuilder()
                .setFrom(from.toString())
                .setTo(to.toString())
                .setAmount(amount.doubleValue())
                .build();
        ConversionReply reply = grpcStub.convert(request);
        return BigDecimal.valueOf(reply.getAmount());
    }
}
