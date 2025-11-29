package org.vl4ds4m.banking.accounts.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.vl4ds4m.banking.accounts.openapi.server.api.AccountsApi;
import org.vl4ds4m.banking.accounts.openapi.server.model.*;
import org.vl4ds4m.banking.accounts.service.AccountService;
import org.vl4ds4m.banking.common.handler.idempotency.Idempotent;
import org.vl4ds4m.banking.common.openapi.model.Currency;
import org.vl4ds4m.banking.common.util.To;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class AccountController implements AccountsApi {

    private final AccountService accountService;

    @Override
    public ResponseEntity<CreateAccountResponse> createAccount(CreateAccountRequest createAccountRequest) {
        long accountNumber = accountService.createAccount(
                createAccountRequest.getCustomerLogin(),
                To.currency(createAccountRequest.getCurrency()));

        var response = new CreateAccountResponse(accountNumber);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<AccountResponse> getAccountByCustomer(String login, Currency currency) {
        var account = accountService.getAccount(login, To.currency(currency));
        var response = new AccountResponse(account.number(), account.money().amount());
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<AccountInfo> getAccountInfo(Long accountNumber) {
        var account = accountService.getAccount(accountNumber);
        var customerLogin = accountService.getAccountOwnerLogin(accountNumber);

        var response = new AccountInfo(
                customerLogin,
                To.restCurrency(account.currency()),
                account.money().amount());
        return ResponseEntity.ok(response);
    }

    @Idempotent
    @Override
    public ResponseEntity<AccountOperationResponse> topUpAccount(
            Long accountNumber,
            UUID idempotencyKey,
            TopUpAccountRequest topUpAccountRequest
    ) {
        var account = accountService.topUpAccount(
                accountNumber,
                To.moneyOrReject(
                        topUpAccountRequest.getAugend(),
                        "Augend"));

        var response = new AccountOperationResponse(
                To.restCurrency(account.currency()),
                account.money().amount());
        return ResponseEntity.ok(response);
    }

    @Idempotent
    @Override
    public ResponseEntity<AccountOperationResponse> withdrawAccount(
            Long accountNumber,
            UUID idempotencyKey,
            WithdrawAccountRequest withdrawAccountRequest
    ) {
        var account = accountService.withdrawMoneyToAccount(
                accountNumber,
                To.moneyOrReject(
                        withdrawAccountRequest.getSubtrahend(),
                        "Subtrahend"));

        var response = new AccountOperationResponse(
                To.restCurrency(account.currency()),
                account.money().amount());
        return ResponseEntity.ok(response);
    }
}
