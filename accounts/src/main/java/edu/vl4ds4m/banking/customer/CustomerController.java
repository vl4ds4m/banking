package edu.vl4ds4m.banking.customer;

import edu.vl4ds4m.banking.currency.Currency;
import edu.vl4ds4m.banking.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(CustomerController.PATH)
public class CustomerController {
    static final String PATH = "/customers";

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public CustomerCreationResponse createCustomer(@RequestBody CustomerCreationRequest request) {
        logger.debug("Accept POST {}: {}", PATH, request);
        return customerService.createCustomer(request);
    }

    @GetMapping("/{customerId}/balance")
    public CustomerBalanceResponse getBalance(
        @PathVariable int customerId,
        @RequestParam Currency currency
    ) {
        logger.debug("Accept GET {}/{}/balance?currency={}", PATH, customerId, currency);
        return customerService.getBalance(customerId, currency);
    }
}
