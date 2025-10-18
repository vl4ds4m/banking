package org.vl4ds4m.banking.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.vl4ds4m.banking.entity.Currency;
import org.vl4ds4m.banking.service.AccountService;
import org.vl4ds4m.banking.api.model.*;

@RestController
@Slf4j
@RequiredArgsConstructor
public class AccountController implements AccountsApi {

    private final AccountService accountService;

    @Override
    public ResponseEntity<CreateAccountResBody> createAccount(CreateAccountReqBody createAccountReqBody) {
        logRequest(HttpMethod.POST, PATH_CREATE_ACCOUNT, createAccountReqBody);
        var account = accountService.createAccount(
                createAccountReqBody.getCustomerName(),
                Currency.valueOf(createAccountReqBody.getCurrency()));
        return ResponseEntity.ok(new CreateAccountResBody(account.getNumber()));
    }

    @Override
    public ResponseEntity<BalanceResBody> getAccountBalance(Long accountNumber) {
        logRequest(HttpMethod.GET, PATH_GET_ACCOUNT_BALANCE, accountNumber);
        var account = accountService.getAccountByNumber(accountNumber);
        return ResponseEntity.ok(new BalanceResBody(
                account.getCurrency().toApiCurrency(),
                account.getMoney().amount()));
    }

    @Override
    public ResponseEntity<AccountOperationResBody> topUpAccount(
            Long accountNumber,
            TopUpAccountReqBody topUpAccountReqBody
    ) {
        logRequest(HttpMethod.PUT, PATH_TOP_UP_ACCOUNT, accountNumber, topUpAccountReqBody);
        accountService.topUpAccount(accountNumber, topUpAccountReqBody.getAugend());
        var account = accountService.getAccountByNumber(accountNumber);
        return ResponseEntity.ok(new AccountOperationResBody(
                account.getCurrency().toApiCurrency(),
                account.getMoney().amount()));
    }

    @Override
    public ResponseEntity<AccountOperationResBody> withdrawAccount(
            Long accountNumber,
            WithdrawAccountReqBody withdrawAccountReqBody
    ) {
        logRequest(HttpMethod.PUT, PATH_WITHDRAW_ACCOUNT, accountNumber, withdrawAccountReqBody);
        accountService.withdrawMoneyToAccount(accountNumber, withdrawAccountReqBody.getSubtrahend());
        var account = accountService.getAccountByNumber(accountNumber);
        return ResponseEntity.ok(new AccountOperationResBody(
                account.getCurrency().toApiCurrency(),
                account.getMoney().amount()));
    }

    private void logRequest(HttpMethod method, String path, Object... requestArgs) {
        log.info("Accept {} {}: {}", method, path, requestArgs);
    }
}
