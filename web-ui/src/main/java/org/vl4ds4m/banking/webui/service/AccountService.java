package org.vl4ds4m.banking.webui.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.vl4ds4m.banking.accounts.openapi.client.api.AccountsApi;
import org.vl4ds4m.banking.accounts.openapi.client.model.AccountInfo;
import org.vl4ds4m.banking.accounts.openapi.client.model.CreateAccountRequest;
import org.vl4ds4m.banking.accounts.openapi.client.model.TopUpAccountRequest;
import org.vl4ds4m.banking.accounts.openapi.client.model.WithdrawAccountRequest;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountsApi accountsApi;

    public void createAccount(CreateAccountRequest request) {
        accountsApi.createAccount(request);
    }

    public AccountInfo getAccountInfo(Long number) {
        return accountsApi.getAccountInfo(number);
    }

    public void topUpAccount(Long number, BigDecimal augend) {
        var request = new TopUpAccountRequest();
        request.setAugend(augend);
        accountsApi.topUpAccount(number, UuidGenerator.random(), request);
    }

    public void withdrawAccount(Long number, BigDecimal subtrahend) {
        var request = new WithdrawAccountRequest();
        request.setSubtrahend(subtrahend);
        accountsApi.withdrawAccount(number, UuidGenerator.random(), request);
    }
}
