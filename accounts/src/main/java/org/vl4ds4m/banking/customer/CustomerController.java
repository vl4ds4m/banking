package org.vl4ds4m.banking.customer;

import com.giffing.bucket4j.spring.boot.starter.context.RateLimitException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.vl4ds4m.banking.currency.Currency;
import org.vl4ds4m.banking.customer.dto.CustomerBalanceResponse;
import org.vl4ds4m.banking.customer.dto.CustomerCreationRequest;
import org.vl4ds4m.banking.customer.dto.CustomerCreationResponse;

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

    @ExceptionHandler
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    public void handleRateLimitException(RateLimitException e) {
        logger.debug("Handle RateLimitException: {}", e.getMessage());
    }
}
