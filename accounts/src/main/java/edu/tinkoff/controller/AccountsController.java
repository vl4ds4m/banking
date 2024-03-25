package edu.tinkoff.controller;

import edu.tinkoff.dao.AccountRepository;
import edu.tinkoff.dao.CustomerRepository;
import edu.tinkoff.model.Account;
import edu.tinkoff.model.Currency;
import edu.tinkoff.model.Customer;
import edu.tinkoff.service.AccountService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@RestController
@RequestMapping(path = "accounts", produces = MediaType.APPLICATION_JSON_VALUE)
public class AccountsController {
    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final AccountService accountService;

    public AccountsController(
            CustomerRepository customerRepository,
            AccountRepository accountRepository,
            AccountService accountService
    ) {
        this.customerRepository = customerRepository;
        this.accountRepository = accountRepository;
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<Object> createAccount(@RequestBody Map<String, Object> requestBody) {
        try {
            int customerId;
            String currency;
            try {
                customerId = (int) requestBody.get("customerId");
                currency = (String) Objects.requireNonNull(requestBody.get("currency"));
            } catch (NullPointerException e) {
                return ResponseEntity.status(400).build();
            }

            if (Currency.fromValue(currency) == null) {
                return ResponseEntity.status(400).build();
            }

            Optional<Customer> optionalCustomer = customerRepository.findById(customerId);
            if (optionalCustomer.isEmpty()) {
                return ResponseEntity.status(400).build();
            }

            Optional<Account> optionalAccount = accountRepository
                    .findByCustomerIdAndCurrency(customerId, currency);
            if (optionalAccount.isPresent()) {
                return ResponseEntity.status(400).build();
            }

            Map<String, Integer> responseBody = Collections.singletonMap(
                    "accountNumber",
                    accountService.createAccount(optionalCustomer.get(), currency).getNumber()
            );
            return ResponseEntity.status(200).body(responseBody);

        } catch (RuntimeException e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<Object> getBalance(@PathVariable int accountNumber) {
        try {
            Optional<Account> optionalAccount = accountRepository.findById(accountNumber);

            if (optionalAccount.isEmpty()) {
                return ResponseEntity.status(400).build();
            }

            Account account = optionalAccount.get();
            return ResponseEntity.status(200).body(Map.of(
                    "amount", accountService.getBalance(account).toString(),
                    "currency", account.getCurrency()
            ));

        } catch (RuntimeException e) {
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/{accountNumber}/top-up")
    public ResponseEntity<Object> topUpAccount(
            @PathVariable int accountNumber,
            @RequestBody Map<String, Object> requestBody
    ) {
        try {
            Double exactAmount = (Double) requestBody.get("amount");
            if (exactAmount == null) {
                return ResponseEntity.status(400).build();
            }

            double amount = BigDecimal.valueOf(exactAmount)
                    .setScale(2, RoundingMode.HALF_EVEN)
                    .doubleValue();

            if (amount <= 0.0) {
                return ResponseEntity.status(400).build();
            }

            Optional<Account> optionalAccount = accountRepository.findById(accountNumber);

            if (optionalAccount.isEmpty()) {
                return ResponseEntity.status(400).build();
            }

            accountService.topUpAccount(optionalAccount.get(), amount);

            return ResponseEntity.status(200).build();

        } catch (RuntimeException e) {
            return ResponseEntity.status(500).build();
        }
    }
}
