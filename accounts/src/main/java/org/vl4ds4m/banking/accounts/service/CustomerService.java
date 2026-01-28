package org.vl4ds4m.banking.accounts.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.vl4ds4m.banking.accounts.dao.CustomerDao;
import org.vl4ds4m.banking.accounts.entity.Account;
import org.vl4ds4m.banking.accounts.entity.Customer;
import org.vl4ds4m.banking.accounts.service.exception.DuplicateEntityException;
import org.vl4ds4m.banking.accounts.service.exception.EntityNotFoundException;
import org.vl4ds4m.banking.accounts.service.validator.CustomerValidator;
import org.vl4ds4m.banking.common.entity.Currency;
import org.vl4ds4m.banking.common.entity.Money;
import org.vl4ds4m.banking.common.exception.ValidationException;
import org.vl4ds4m.banking.common.util.To;

import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerDao customerDao;

    private final CustomerValidator customerValidator;

    private final ConverterService converterService;

    public Customer getCustomer(String login) {
        checkCustomerExists(login);
        return customerDao.getByLogin(login);
    }

    public Set<Customer> getCustomers() {
        return customerDao.getAll();
    }

    // TODO
    // @Observed
    public void createCustomer(Customer newCustomer) {
        var login = newCustomer.login();
        if (customerDao.existsByLogin(login)) {
            throw new DuplicateEntityException(Customer.class, login);
        }

        var errors = customerValidator.validateObject(newCustomer);
        if (errors.hasErrors()) {
            throw new ValidationException(errors);
        }

        customerDao.create(newCustomer);
        log.info("{} created", To.string(Customer.class, login));
    }

    public Set<Account> getCustomerAccounts(String login) {
        checkCustomerExists(login);
        return customerDao.getAccounts(login);
    }

    // TODO
    // @Observed
    public Money getCustomerBalance(String login, Currency currency) {
        checkCustomerExists(login);

        return customerDao.getAccounts(login).stream()
                .map(a -> converterService.convert(
                        a.currency(),
                        currency,
                        a.money()))
                .reduce(Money.empty(), Money::add);
    }

    private void checkCustomerExists(String login) {
        if (!customerDao.existsByLogin(login)) {
            throw new EntityNotFoundException(Customer.class, login);
        }
    }
}
