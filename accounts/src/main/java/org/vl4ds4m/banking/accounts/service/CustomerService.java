package org.vl4ds4m.banking.accounts.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.vl4ds4m.banking.accounts.dao.CustomerDao;
import org.vl4ds4m.banking.accounts.entity.Customer;
import org.vl4ds4m.banking.accounts.service.expection.DuplicateEntityException;
import org.vl4ds4m.banking.accounts.service.expection.EntityNotFoundException;
import org.vl4ds4m.banking.common.exception.ValidationException;
import org.vl4ds4m.banking.accounts.service.validator.CustomerValidator;
import org.vl4ds4m.banking.common.entity.Currency;
import org.vl4ds4m.banking.common.entity.Money;
import org.vl4ds4m.banking.common.util.To;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerDao customerDao;

    private final CustomerValidator customerValidator;

    private final ConverterService converterService;

    public Customer getCustomer(String nickname) {
        checkCustomerExists(nickname);
        return customerDao.getByNickname(nickname);
    }

    public void createCustomer(Customer newCustomer) {
        var nickname = newCustomer.nickname();
        if (customerDao.existsByNickname(nickname)) {
            throw new DuplicateEntityException(Customer.class, nickname);
        }

        var errors = customerValidator.validateObject(newCustomer);
        if (errors.hasErrors()) {
            throw new ValidationException(errors);
        }

        customerDao.create(newCustomer);
        log.info("{} created", To.string(Customer.class, nickname));
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

    private void checkCustomerExists(String nickname) {
        if (!customerDao.existsByNickname(nickname)) {
            throw new EntityNotFoundException(Customer.class, nickname);
        }
    }
}
