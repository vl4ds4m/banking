package org.vl4ds4m.banking.accounts.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.vl4ds4m.banking.accounts.entity.Customer;
import org.vl4ds4m.banking.accounts.openapi.server.api.CustomersApi;
import org.vl4ds4m.banking.accounts.openapi.server.model.BalanceResponse;
import org.vl4ds4m.banking.accounts.openapi.server.model.CreateCustomerRequest;
import org.vl4ds4m.banking.accounts.service.CustomerService;
import org.vl4ds4m.banking.common.openapi.model.Currency;
import org.vl4ds4m.banking.common.util.To;

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
                To.currency(currency));

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
