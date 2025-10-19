package org.vl4ds4m.banking.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.vl4ds4m.banking.api.model.BalanceResponse;
import org.vl4ds4m.banking.api.model.CreateCustomerRequest;
import org.vl4ds4m.banking.api.model.Currency;
import org.vl4ds4m.banking.service.CustomerService;

@RestController
@Slf4j
@RequiredArgsConstructor
public class CustomerController implements CustomersApi {

    private final CustomerService customerService;

    @Override
    public ResponseEntity<Void> createCustomer(CreateCustomerRequest createCustomerRequest) {
        logRequest(HttpMethod.POST, PATH_CREATE_CUSTOMER, createCustomerRequest);
        customerService.createCustomer(createCustomerRequest);
        return ResponseEntity.ok(null);
    }

    @Override
    public ResponseEntity<BalanceResponse> getCustomerBalance(String customerName, Currency currency) {
        logRequest(HttpMethod.GET, PATH_GET_CUSTOMER_BALANCE, customerName, currency);
        var response = customerService.getCustomerBalance(customerName, currency);
        return ResponseEntity.ok(response);
    }

    private void logRequest(HttpMethod method, String path, Object... requestArgs) {
        log.info("Accept {} {}: {}", method, path, requestArgs);
    }
}
