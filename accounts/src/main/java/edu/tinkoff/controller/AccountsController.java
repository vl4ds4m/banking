package edu.tinkoff.controller;

import edu.tinkoff.dao.AccountRepository;
import edu.tinkoff.dao.CustomerRepository;
import edu.tinkoff.model.Account;
import edu.tinkoff.model.Currency;
import edu.tinkoff.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@RestController
@RequestMapping(path = "accounts", produces = MediaType.APPLICATION_JSON_VALUE)
public class AccountsController {

    @Value("${services.converter.url}")
    private String converterUrl;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AccountRepository accountRepository;

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

            Account account = new Account(optionalCustomer.get(), currency);
            account = accountRepository.save(account);

            Map<String, Integer> responseBody = Collections.singletonMap(
                    "accountNumber", account.getNumber()
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
            Map<String, Object> responseBody = Map.of(
                    "amount", account.getAmount(),
                    "currency", account.getCurrency()
            );
            return ResponseEntity.status(200).body(responseBody);

        } catch (RuntimeException e) {
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/{accountNumber}/top-up")
    public ResponseEntity<Object> topUpAccount(
            @PathVariable int accountNumber,
            @RequestBody Map<String, Double> requestBody
    ) {
        try {
            Double exactAmount = requestBody.get("amount");
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

            Account account = optionalAccount.get();
            account.setAmount(account.getAmount() + amount);
            accountRepository.save(account);

            return ResponseEntity.status(200).build();

        } catch (RuntimeException e) {
            return ResponseEntity.status(500).build();
        }
    }
}
