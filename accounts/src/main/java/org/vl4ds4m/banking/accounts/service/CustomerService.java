package org.vl4ds4m.banking.accounts.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.vl4ds4m.banking.accounts.dao.CustomerDao;
import org.vl4ds4m.banking.accounts.entity.Customer;
import org.vl4ds4m.banking.accounts.service.expection.DuplicateEntityException;
import org.vl4ds4m.banking.accounts.service.expection.EntityNotFoundException;
import org.vl4ds4m.banking.accounts.service.expection.ServiceException;
import org.vl4ds4m.banking.common.entity.Currency;
import org.vl4ds4m.banking.common.entity.Money;

import java.time.LocalDate;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerDao customerDao;

    private final ConverterService converterService;

    public Customer getCustomer(String customerName) {
        checkCustomerExists(customerName);
        return customerDao.getByName(customerName);
    }

    public void createCustomer(
            String name,
            String firstName,
            String lastName,
            LocalDate birthDate
    ) {
        if (customerDao.existsByName(name)) {
            throw new DuplicateEntityException(Customer.logStr(name));
        }

        var now = LocalDate.now();
        var maxBirthDate = now.minusYears(14);
        var minBirthDate = now.minusYears(121).plusDays(1);

        if (birthDate.isAfter(maxBirthDate) || birthDate.isBefore(minBirthDate)) {
            throw new ServiceException("Customer age must be in range of 14 to 120 years. " +
                    "Passed birth date = " + birthDate);
        }

        var customer = new Customer(name, firstName, lastName, birthDate);
        customerDao.create(customer);
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
