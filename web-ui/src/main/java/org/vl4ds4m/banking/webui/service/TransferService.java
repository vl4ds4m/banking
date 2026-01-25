package org.vl4ds4m.banking.webui.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.vl4ds4m.banking.accounts.openapi.client.api.AccountsApi;
import org.vl4ds4m.banking.accounts.openapi.client.api.TransferApi;
import org.vl4ds4m.banking.accounts.openapi.client.model.AccountNumberResponse;
import org.vl4ds4m.banking.accounts.openapi.client.model.TransferRequest;
import org.vl4ds4m.banking.common.openapi.model.Currency;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class TransferService {

    private final AccountsApi accountsApi;

    private final TransferApi transferApi;

    public void transfer(
            String senderLogin,
            Currency senderCurrency,
            String receiverLogin,
            Currency receiverCurrency,
            BigDecimal amount
    ) {
        AccountNumberResponse sender = accountsApi.getAccountNumberByCustomer(senderLogin, senderCurrency);
        AccountNumberResponse receiver = accountsApi.getAccountNumberByCustomer(receiverLogin, receiverCurrency);

        var request = new TransferRequest();
        request.setSenderAccountNumber(sender.getNumber());
        request.setReceiverAccountNumber(receiver.getNumber());
        request.setSenderCurrencyAmount(amount);

        transferApi.transfer(UuidGenerator.random(), request);
    }

    public void transfer(
            long senderNumber,
            String receiverLogin,
            Currency receiverCurrency,
            BigDecimal amount
    ) {
        AccountNumberResponse receiver = accountsApi.getAccountNumberByCustomer(receiverLogin, receiverCurrency);

        var request = new TransferRequest();
        request.setSenderAccountNumber(senderNumber);
        request.setReceiverAccountNumber(receiver.getNumber());
        request.setSenderCurrencyAmount(amount);

        transferApi.transfer(UuidGenerator.random(), request);
    }
}
