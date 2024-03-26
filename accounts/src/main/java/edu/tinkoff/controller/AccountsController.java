package edu.tinkoff.controller;

import edu.tinkoff.dto.AccountBalance;
import edu.tinkoff.dto.AccountMessage;
import edu.tinkoff.service.AccountService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "accounts", produces = MediaType.APPLICATION_JSON_VALUE)
public class AccountsController {
    private final AccountService accountService;

    public AccountsController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<AccountMessage> createAccount(@RequestBody AccountMessage message) {
        return accountService.createAccount(message)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<AccountBalance> getBalance(@PathVariable int accountNumber) {
        return accountService.getBalance(accountNumber)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @PostMapping("/{accountNumber}/top-up")
    public ResponseEntity<?> topUpAccount(
            @PathVariable int accountNumber,
            @RequestBody AccountBalance amount
    ) {
        return accountService.topUpAccount(accountNumber, amount) ?
                ResponseEntity.ok().build() :
                ResponseEntity.badRequest().build();
    }
}
