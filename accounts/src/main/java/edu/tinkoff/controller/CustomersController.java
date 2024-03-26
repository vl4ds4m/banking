package edu.tinkoff.controller;

import edu.tinkoff.dto.Currency;
import edu.tinkoff.dto.Customer;
import edu.tinkoff.dto.CustomerBalance;
import edu.tinkoff.service.CustomerService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "customers", produces = MediaType.APPLICATION_JSON_VALUE)
public class CustomersController {
    private final CustomerService customerService;

    public CustomersController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer) {
        return customerService.createCustomer(customer)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @GetMapping("/{customerId}/balance")
    public ResponseEntity<CustomerBalance> getBalance(
            @PathVariable int customerId,
            @RequestParam Currency currency
    ) {
        return customerService.getBalance(customerId, currency)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }
}
