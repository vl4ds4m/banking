package edu.tinkoff.controller;

import edu.tinkoff.dto.*;
import edu.tinkoff.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "customers", produces = MediaType.APPLICATION_JSON_VALUE)
public class CustomersController {
    private static final Logger log = LoggerFactory.getLogger(CustomersController.class);

    private final CustomerService customerService;

    public CustomersController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public CustomerCreationResponse createCustomer(@RequestBody CustomerCreationRequest request) {
        log.info("Accept a request to create a customer");
        return customerService.createCustomer(request);
    }

    @GetMapping("/{customerId}/balance")
    public CustomerBalanceResponse getBalance(
            @PathVariable int customerId,
            @RequestParam Currency currency
    ) {
        log.info("Accept a request to get a customer balance");
        return customerService.getBalance(customerId, currency);
    }
}
