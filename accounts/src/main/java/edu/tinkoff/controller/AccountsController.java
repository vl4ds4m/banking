package edu.tinkoff.controller;

import edu.tinkoff.dto.*;
import edu.tinkoff.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "accounts", produces = MediaType.APPLICATION_JSON_VALUE)
public class AccountsController {
    private static final Logger log = LoggerFactory.getLogger(AccountsController.class);

    private final AccountService accountService;

    public AccountsController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public AccountCreationResponse createAccount(@RequestBody AccountCreationRequest message) {
        log.info("Accept a request to create an account");
        return accountService.createAccount(message);
    }

    @GetMapping("/{accountNumber}")
    public AccountBalance getBalance(@PathVariable int accountNumber) {
        log.info("Accept a request to get an account balance");
        return accountService.getBalance(accountNumber);
    }

    @PostMapping("/{accountNumber}/top-up")
    public TransactionResponse topUpAccount(
            @PathVariable int accountNumber,
            @RequestBody AccountTopUpRequest request
    ) {
        log.info("Accept a request to top up an account");
        return accountService.topUpAccount(accountNumber, request);
    }

    @GetMapping("/{accountNumber}/transactions")
    public List<TransactionResponse> getTransactions(@PathVariable int accountNumber) {
        log.info("Accept a request to get account transactions");
        return accountService.getTransactions(accountNumber);
    }
}
