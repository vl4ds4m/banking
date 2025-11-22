package org.vl4ds4m.banking.accounts.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.vl4ds4m.banking.accounts.openapi.server.api.CustomersApi;
import org.vl4ds4m.banking.accounts.openapi.server.model.*;
import org.vl4ds4m.banking.accounts.service.CustomerService;
import org.vl4ds4m.banking.common.openapi.model.Currency;
import org.vl4ds4m.banking.common.util.To;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class CustomerController implements CustomersApi {

    private final CustomerService customerService;

    @Override
    public ResponseEntity<List<CustomerNames>> getCustomers() {
        var response = customerService.getCustomers()
                .stream()
                .map(c -> new CustomerNames(
                        c.nickname(),
                        c.forename(),
                        c.surname()))
                .toList();
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> createCustomer(Customer customer) {
        var newCustomer = new org.vl4ds4m.banking.accounts.entity.Customer(
                customer.getNickname(),
                customer.getForename(),
                customer.getSurname(),
                customer.getBirthdate());
        customerService.createCustomer(newCustomer);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<CustomerInfo> getCustomerInfo(String nickname) {
        var customer = Optional.of(customerService.getCustomer(nickname))
                .map(c -> new Customer(
                        c.nickname(),
                        c.forename(),
                        c.surname(),
                        c.birthdate()))
                .get();
        var accounts = customerService.getCustomerAccounts(nickname)
                .stream()
                .map(a -> new Account(
                        a.number(),
                        To.restCurrency(a.currency()),
                        a.money().amount()))
                .toList();
        var response = new CustomerInfo(customer, accounts);
        return ResponseEntity.ok(response);
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
