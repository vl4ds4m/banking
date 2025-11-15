package org.vl4ds4m.banking.accounts.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.vl4ds4m.banking.accounts.api.http.TransferApi;
import org.vl4ds4m.banking.accounts.api.http.model.TransferRequest;
import org.vl4ds4m.banking.accounts.api.http.model.TransferResponse;
import org.vl4ds4m.banking.accounts.service.TransferService;
import org.vl4ds4m.banking.common.util.To;

@RestController
@RequiredArgsConstructor
public class TransferController implements TransferApi {

    private final TransferService service;

    @Override
    public ResponseEntity<TransferResponse> transfer(TransferRequest transferRequest) {
        var result = service.transferMoney(
                transferRequest.getSenderAccountNumber(),
                transferRequest.getReceiverAccountNumber(),
                To.moneyOrReject(
                        transferRequest.getSenderCurrencyAmount(),
                        "Amount to transfer"));

        var response = new TransferResponse(
                result.totalSenderMoney().amount(),
                result.totalReceiverMoney().amount());
        return ResponseEntity.ok(response);
    }
}
