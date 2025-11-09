package org.vl4ds4m.banking.accounts.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.vl4ds4m.banking.accounts.dao.CustomerDao;
import org.vl4ds4m.banking.accounts.entity.Customer;
import org.vl4ds4m.banking.accounts.service.expection.DuplicateEntityException;
import org.vl4ds4m.banking.accounts.service.expection.EntityNotFoundException;
import org.vl4ds4m.banking.accounts.service.expection.ValidationException;
import org.vl4ds4m.banking.accounts.service.validation.CustomerValidator;
import org.vl4ds4m.banking.common.entity.Currency;
import org.vl4ds4m.banking.common.entity.Money;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerDao customerDao;

    private final CustomerValidator customerValidator;

    private final ConverterService converterService;

    public Customer getCustomer(String customerName) {
        checkCustomerExists(customerName);
        return customerDao.getByName(customerName);
    }

    public void createCustomer(Customer newCustomer) {
        var name = newCustomer.name();
        if (customerDao.existsByName(name)) {
            throw new DuplicateEntityException(Customer.logStr(name));
        }

        var errors = customerValidator.validateObject(newCustomer);
        if (errors.hasErrors()) {
            throw ValidationException.with(errors);
        }

        customerDao.create(newCustomer);
        log.info("{} created", Customer.logStr(name));
    }

    public Money getCustomerBalance(String customerName, Currency currency) {
        checkCustomerExists(customerName);

        return customerDao.getAccounts(customerName).stream()
                .map(a -> converterService.convert(
                        a.currency(),
                        currency,
                        a.money()))
                .reduce(Money.empty(), Money::add);
    }

    private void checkCustomerExists(String customerName) {
        if (!customerDao.existsByName(customerName)) {
            throw new EntityNotFoundException(Customer.logStr(customerName));
        }
    }
}
