package org.vl4ds4m.banking.accounts.api;

import com.giffing.bucket4j.spring.boot.starter.context.RateLimiting;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.vl4ds4m.banking.accounts.auth.AccountsAuthorizationManager;
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

    private final AccountsAuthorizationManager authorizationManager;

    @Override
    public ResponseEntity<List<CustomerNames>> getCustomers() {
        var response = customerService.getCustomers()
                .stream()
                .map(c -> new CustomerNames(
                        c.login(),
                        c.forename(),
                        c.surname()))
                .toList();
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> createCustomer(Customer customer) {
        var newCustomer = new org.vl4ds4m.banking.accounts.entity.Customer(
                customer.getLogin(),
                customer.getForename(),
                customer.getSurname(),
                customer.getBirthdate());
        customerService.createCustomer(newCustomer);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<CustomerInfo> getCustomerInfo(String login) {
        authorizationManager.authorizeCustomer(login);

        var customer = Optional.of(customerService.getCustomer(login))
                .map(c -> new Customer(
                        c.login(),
                        c.forename(),
                        c.surname(),
                        c.birthdate()))
                .get();
        var accounts = customerService.getCustomerAccounts(login)
                .stream()
                .map(a -> new Account(
                        a.number(),
                        To.restCurrency(a.currency()),
                        a.money().amount()))
                .toList();

        var response = new CustomerInfo(customer, accounts);
        return ResponseEntity.ok(response);
    }

    @RateLimiting(
        name = "customer-balance",
        cacheKey = "#customerLogin",
        ratePerMethod = true)
    @Override
    public ResponseEntity<BalanceResponse> getCustomerBalance(String customerLogin, Currency currency) {
        authorizationManager.authorizeCustomer(customerLogin);

        var money = customerService.getCustomerBalance(
                customerLogin,
                To.currency(currency));

        var response = new BalanceResponse(currency, money.amount());
        return ResponseEntity.ok(response);
    }

}
