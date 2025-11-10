package org.vl4ds4m.banking.accounts.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.vl4ds4m.banking.accounts.api.util.CurrencyConverter;
import org.vl4ds4m.banking.accounts.service.AccountService;
import org.vl4ds4m.banking.accounts.api.model.*;
import org.vl4ds4m.banking.common.util.To;

@RestController
@Slf4j
@RequiredArgsConstructor
public class AccountController implements AccountsApi {

    private final AccountService accountService;

    @Override
    public ResponseEntity<CreateAccountResponse> createAccount(CreateAccountRequest createAccountRequest) {
        logRequest(HttpMethod.POST, PATH_CREATE_ACCOUNT, createAccountRequest);

        long accountNumber = accountService.createAccount(
                createAccountRequest.getCustomerName(),
                CurrencyConverter.toEntity(createAccountRequest.getCurrency()));

        var response = new CreateAccountResponse(accountNumber);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<BalanceResponse> getAccountBalance(Long accountNumber) {
        logRequest(HttpMethod.GET, PATH_GET_ACCOUNT_BALANCE, accountNumber);

        var account = accountService.getAccount(accountNumber);

        var response = new BalanceResponse(
                CurrencyConverter.toApi(account.currency()),
                account.money().amount());
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<AccountOperationResponse> topUpAccount(
            Long accountNumber,
            TopUpAccountRequest topUpAccountRequest
    ) {
        logRequest(HttpMethod.PUT, PATH_TOP_UP_ACCOUNT, accountNumber, topUpAccountRequest);

        var account = accountService.topUpAccount(
                accountNumber,
                To.moneyOrReject(
                        topUpAccountRequest.getAugend(),
                        "Augend"));

        var response = new AccountOperationResponse(
                CurrencyConverter.toApi(account.currency()),
                account.money().amount());
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<AccountOperationResponse> withdrawAccount(
            Long accountNumber,
            WithdrawAccountRequest withdrawAccountRequest
    ) {
        logRequest(HttpMethod.PUT, PATH_WITHDRAW_ACCOUNT, accountNumber, withdrawAccountRequest);

        var account = accountService.withdrawMoneyToAccount(
                accountNumber,
                To.moneyOrReject(
                        withdrawAccountRequest.getSubtrahend(),
                        "Subtrahend"));

        var response = new AccountOperationResponse(
                CurrencyConverter.toApi(account.currency()),
                account.money().amount());
        return ResponseEntity.ok(response);
    }

    private void logRequest(HttpMethod method, String path, Object... requestArgs) {
        log.info("Accept {} {}: {}", method, path, requestArgs);
    }
}
