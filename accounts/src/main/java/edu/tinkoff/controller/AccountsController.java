package edu.tinkoff.controller;

import edu.tinkoff.dto.AccountBalance;
import edu.tinkoff.dto.AccountCreationRequest;
import edu.tinkoff.dto.AccountCreationResponse;
import edu.tinkoff.dto.AccountTopUpRequest;
import edu.tinkoff.service.AccountService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

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
    public void topUpAccount(
            @PathVariable int accountNumber,
            @RequestBody AccountTopUpRequest request
    ) {
        accountService.topUpAccount(accountNumber, request);
    }
}
