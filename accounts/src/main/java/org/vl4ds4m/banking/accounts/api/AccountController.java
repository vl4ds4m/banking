package org.vl4ds4m.banking.accounts.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.vl4ds4m.banking.accounts.openapi.server.api.AccountsApi;
import org.vl4ds4m.banking.accounts.openapi.server.model.*;
import org.vl4ds4m.banking.accounts.service.AccountService;
import org.vl4ds4m.banking.common.util.To;

@RestController
@RequiredArgsConstructor
public class AccountController implements AccountsApi {

    private final AccountService accountService;

    @Override
    public ResponseEntity<CreateAccountResponse> createAccount(CreateAccountRequest createAccountRequest) {
        long accountNumber = accountService.createAccount(
                createAccountRequest.getCustomerName(),
                To.currency(createAccountRequest.getCurrency()));

        var response = new CreateAccountResponse(accountNumber);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<BalanceResponse> getAccountBalance(Long accountNumber) {
        var account = accountService.getAccount(accountNumber);

        var response = new BalanceResponse(
                To.currency(account.currency()),
                account.money().amount());
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<AccountOperationResponse> topUpAccount(
            Long accountNumber,
            TopUpAccountRequest topUpAccountRequest
    ) {
        var account = accountService.topUpAccount(
                accountNumber,
                To.moneyOrReject(
                        topUpAccountRequest.getAugend(),
                        "Augend"));

        var response = new AccountOperationResponse(
                To.currency(account.currency()),
                account.money().amount());
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<AccountOperationResponse> withdrawAccount(
            Long accountNumber,
            WithdrawAccountRequest withdrawAccountRequest
    ) {
        var account = accountService.withdrawMoneyToAccount(
                accountNumber,
                To.moneyOrReject(
                        withdrawAccountRequest.getSubtrahend(),
                        "Subtrahend"));

        var response = new AccountOperationResponse(
                To.currency(account.currency()),
                account.money().amount());
        return ResponseEntity.ok(response);
    }

    // TODO
    // @GetMapping("/{accountNumber}/transactions")
    // public List<TransactionResponse> getTransactions(@PathVariable int accountNumber) {
    //     logger.debug("Accept GET {}/{}/transactions", PATH, accountNumber);
    //     return accountService.getTransactions(accountNumber);
    // }
}
