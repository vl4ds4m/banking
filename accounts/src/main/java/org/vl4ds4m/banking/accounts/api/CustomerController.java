package org.vl4ds4m.banking.accounts.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.vl4ds4m.banking.accounts.api.converter.CurrencyConverter;
import org.vl4ds4m.banking.accounts.api.http.CustomersApi;
import org.vl4ds4m.banking.accounts.api.http.model.BalanceResponse;
import org.vl4ds4m.banking.accounts.api.http.model.CreateCustomerRequest;
import org.vl4ds4m.banking.accounts.api.http.model.Currency;
import org.vl4ds4m.banking.accounts.entity.Customer;
import org.vl4ds4m.banking.accounts.service.CustomerService;

@RestController
@RequiredArgsConstructor
public class CustomerController implements CustomersApi {

    private final CustomerService customerService;

    @Override
    public ResponseEntity<Void> createCustomer(CreateCustomerRequest createCustomerRequest) {
        var newCustomer = new Customer(
                createCustomerRequest.getCustomerName(),
                createCustomerRequest.getFirstName(),
                createCustomerRequest.getLastName(),
                createCustomerRequest.getBirthDate());
        customerService.createCustomer(newCustomer);

        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<BalanceResponse> getCustomerBalance(String customerName, Currency currency) {
        var money = customerService.getCustomerBalance(
                customerName,
                CurrencyConverter.toEntity(currency));

        var response = new BalanceResponse(currency, money.amount());
        return ResponseEntity.ok(response);
    }

    // TODO
    // @ExceptionHandler
    // @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    // public void handleRateLimitException(RateLimitException e) {
    //     logger.debug("Handle RateLimitException: {}", e.getMessage());
    // }
}
