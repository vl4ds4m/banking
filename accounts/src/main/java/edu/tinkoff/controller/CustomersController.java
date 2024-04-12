package edu.tinkoff.controller;

import edu.tinkoff.dto.*;
import edu.tinkoff.service.CustomerService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "customers", produces = MediaType.APPLICATION_JSON_VALUE)
public class CustomersController {
    private final CustomerService customerService;

    public CustomersController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public CustomerCreationResponse createCustomer(@RequestBody CustomerCreationRequest request) {
        return customerService.createCustomer(request);
    }

    @GetMapping("/{customerId}/balance")
    public CustomerBalanceResponse getBalance(
            @PathVariable int customerId,
            @RequestParam Currency currency
    ) {
        return customerService.getBalance(customerId, currency);
    }
}
