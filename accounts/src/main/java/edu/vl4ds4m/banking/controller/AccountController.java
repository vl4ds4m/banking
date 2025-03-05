package edu.vl4ds4m.banking.controller;

import edu.vl4ds4m.banking.dto.*;
import edu.vl4ds4m.banking.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(AccountController.PATH)
public class AccountController {
    static final String PATH = "/accounts";

    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public AccountCreationResponse createAccount(@RequestBody AccountCreationRequest request) {
        logger.debug("Accept POST {}: {}", PATH, request);
        return accountService.createAccount(request);
    }

    @GetMapping("/{accountNumber}")
    public AccountBalance getBalance(@PathVariable int accountNumber) {
        logger.debug("Accept GET {}/{}", PATH, accountNumber);
        return accountService.getBalance(accountNumber);
    }

    @PostMapping("/{accountNumber}/top-up")
    public TransactionResponse topUpAccount(
            @PathVariable int accountNumber,
            @RequestBody AccountTopUpRequest request
    ) {
        logger.debug("Accept POST {}/{}/top-up: {}", PATH, accountNumber, request);
        return accountService.topUpAccount(accountNumber, request);
    }

    @GetMapping("/{accountNumber}/transactions")
    public List<TransactionResponse> getTransactions(@PathVariable int accountNumber) {
        logger.debug("Accept GET {}/{}/transactions", PATH, accountNumber);
        return accountService.getTransactions(accountNumber);
    }
}
