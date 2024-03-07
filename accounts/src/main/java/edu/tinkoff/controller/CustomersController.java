package edu.tinkoff.controller;

import edu.tinkoff.dao.AccountRepository;
import edu.tinkoff.dao.CustomerRepository;
import edu.tinkoff.model.Account;
import edu.tinkoff.model.Currency;
import edu.tinkoff.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

@RestController
@RequestMapping(path = "customers", produces = MediaType.APPLICATION_JSON_VALUE)
public class CustomersController {

    @Autowired
    private ConverterInvoker utils;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AccountRepository accountRepository;

    @PostMapping
    public ResponseEntity<Object> createCustomer(
            @RequestBody Map<String, String> requestBody
    ) {
        try {
            String firstName;
            String lastName;
            LocalDate birthDate;
            try {
                firstName = Objects.requireNonNull(requestBody.get("firstName"));
                lastName = Objects.requireNonNull(requestBody.get("lastName"));
                birthDate = LocalDate.parse(requestBody.get("birthDay"));
            } catch (NullPointerException | DateTimeParseException e) {
                return ResponseEntity.status(400).build();
            }

            int roughCustomerAge = LocalDate.now().getYear() - birthDate.getYear();
            if (roughCustomerAge < 14 || roughCustomerAge > 120) {
                return ResponseEntity.status(400).build();
            }

            Customer customer = new Customer(firstName, lastName, birthDate);
            customer = customerRepository.save(customer);

            Map<String, Integer> responseBody = Collections.singletonMap(
                    "customerId", customer.getId()
            );
            return ResponseEntity.status(200).body(responseBody);

        } catch (RuntimeException e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/{customerId}/balance")
    public ResponseEntity<Object> getBalance(
            @PathVariable int customerId,
            @RequestParam String currency
    ) {
        try {
            if (Currency.fromValue(currency) == null) {
                return ResponseEntity.status(400).build();
            }

            Optional<Customer> optionalCustomer = customerRepository.findById(customerId);

            if (optionalCustomer.isEmpty()) {
                return ResponseEntity.status(400).build();
            }

            List<Account> accounts = accountRepository.findAllByCustomerId(customerId);

            BigDecimal balance = BigDecimal.ZERO;

            for (Account account : accounts) {
                if (account.getAmount() > 0) {
                    Map<String, Object> responseBody = utils.convert(
                            account.getCurrency(),
                            currency,
                            account.getAmount()
                    );
                    BigDecimal amount = BigDecimal.valueOf((double) responseBody.get("amount"));
                    balance = balance.add(amount);
                }
            }

            Map<String, Object> responseBody = Map.of(
                    "balance", balance.doubleValue(),
                    "currency", currency
            );
            return ResponseEntity.status(200).body(responseBody);

        } catch (RuntimeException e) {
            return ResponseEntity.status(500).build();
        }
    }
}
