package org.vl4ds4m.banking.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.vl4ds4m.banking.service.AccountService;
import org.vl4ds4m.banking.api.model.*;

@RestController
@Slf4j
@RequiredArgsConstructor
public class AccountController implements AccountsApi {

    private final AccountService accountService;

    @Override
    public ResponseEntity<CreateAccountResponse> createAccount(CreateAccountRequest createAccountRequest) {
        logRequest(HttpMethod.POST, PATH_CREATE_ACCOUNT, createAccountRequest);
        var response = accountService.createAccount(createAccountRequest);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<BalanceResponse> getAccountBalance(Long accountNumber) {
        logRequest(HttpMethod.GET, PATH_GET_ACCOUNT_BALANCE, accountNumber);
        var response = accountService.getAccountBalance(accountNumber);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<AccountOperationResponse> topUpAccount(
            Long accountNumber,
            TopUpAccountRequest topUpAccountRequest
    ) {
        logRequest(HttpMethod.PUT, PATH_TOP_UP_ACCOUNT, accountNumber, topUpAccountRequest);
        var response = accountService.topUpAccount(accountNumber, topUpAccountRequest);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<AccountOperationResponse> withdrawAccount(
            Long accountNumber,
            WithdrawAccountRequest withdrawAccountRequest
    ) {
        logRequest(HttpMethod.PUT, PATH_WITHDRAW_ACCOUNT, accountNumber, withdrawAccountRequest);
        var response = accountService.withdrawMoneyToAccount(accountNumber, withdrawAccountRequest);
        return ResponseEntity.ok(response);
    }

    private void logRequest(HttpMethod method, String path, Object... requestArgs) {
        log.info("Accept {} {}: {}", method, path, requestArgs);
    }
}
