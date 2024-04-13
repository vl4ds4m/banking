package edu.tinkoff.controller;

import edu.tinkoff.dto.*;
import edu.tinkoff.service.AccountService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "accounts", produces = MediaType.APPLICATION_JSON_VALUE)
public class AccountsController {
    private final AccountService accountService;

    public AccountsController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public AccountCreationResponse createAccount(@RequestBody AccountCreationRequest message) {
        return accountService.createAccount(message);
    }

    @GetMapping("/{accountNumber}")
    public AccountBalance getBalance(@PathVariable int accountNumber) {
        return accountService.getBalance(accountNumber);
    }

    @PostMapping("/{accountNumber}/top-up")
    public TransactionResponse topUpAccount(
            @PathVariable int accountNumber,
            @RequestBody AccountTopUpRequest request
    ) {
        return accountService.topUpAccount(accountNumber, request);
    }

    @GetMapping("/{accountNumber}/transactions")
    public List<TransactionResponse> getTransactions(@PathVariable int accountNumber) {
        return accountService.getTransactions(accountNumber);
    }
}
