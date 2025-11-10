package org.vl4ds4m.banking.accounts.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.vl4ds4m.banking.accounts.api.model.BalanceResponse;
import org.vl4ds4m.banking.accounts.api.model.CreateCustomerRequest;
import org.vl4ds4m.banking.accounts.api.model.Currency;
import org.vl4ds4m.banking.accounts.api.util.CurrencyConverter;
import org.vl4ds4m.banking.accounts.entity.Customer;
import org.vl4ds4m.banking.accounts.service.CustomerService;

@RestController
@Slf4j
@RequiredArgsConstructor
public class CustomerController implements CustomersApi {

    private final CustomerService customerService;

    @Override
    public ResponseEntity<Void> createCustomer(CreateCustomerRequest createCustomerRequest) {
        logRequest(HttpMethod.POST, PATH_CREATE_CUSTOMER, createCustomerRequest);

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
        logRequest(HttpMethod.GET, PATH_GET_CUSTOMER_BALANCE, customerName, currency);

        var money = customerService.getCustomerBalance(
                customerName,
                CurrencyConverter.toEntity(currency));

        var response = new BalanceResponse(currency, money.amount());
        return ResponseEntity.ok(response);
    }

    private void logRequest(HttpMethod method, String path, Object... requestArgs) {
        log.info("Accept {} {}: {}", method, path, requestArgs);
    }

    // TODO
    // @ExceptionHandler
    // @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    // public void handleRateLimitException(RateLimitException e) {
    //     logger.debug("Handle RateLimitException: {}", e.getMessage());
    // }
}
