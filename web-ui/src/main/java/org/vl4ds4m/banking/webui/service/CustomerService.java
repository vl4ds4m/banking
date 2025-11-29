package org.vl4ds4m.banking.webui.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.vl4ds4m.banking.accounts.openapi.client.api.CustomersApi;
import org.vl4ds4m.banking.accounts.openapi.client.model.Account;
import org.vl4ds4m.banking.accounts.openapi.client.model.Customer;
import org.vl4ds4m.banking.accounts.openapi.client.model.CustomerInfo;
import org.vl4ds4m.banking.accounts.openapi.client.model.CustomerNames;

import java.util.Comparator;
import java.util.List;


@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomersApi customerApi;

    public List<CustomerNames> getAllCustomers() {
        return customerApi.getCustomers()
                .stream()
                .sorted(Comparator.comparing(CustomerNames::getSurname)
                        .thenComparing(CustomerNames::getForename))
                .toList();
    }

    public CustomerInfo getCustomer(String login) {
        CustomerInfo info = customerApi.getCustomerInfo(login);
        List<Account> sortedAccounts = info.getAccounts()
                .stream()
                .sorted(Comparator.comparing(Account::getNumber))
                .toList();
        info.setAccounts(sortedAccounts);
        return info;
    }

    public void createCustomer(Customer customer) {
        customerApi.createCustomer(customer);
    }
}
