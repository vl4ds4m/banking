package org.vl4ds4m.banking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.vl4ds4m.banking.api.model.BalanceResponse;
import org.vl4ds4m.banking.api.model.CreateCustomerRequest;
import org.vl4ds4m.banking.api.model.Currency;
import org.vl4ds4m.banking.converter.ConverterService;
import org.vl4ds4m.banking.entity.Customer;
import org.vl4ds4m.banking.entity.Money;
import org.vl4ds4m.banking.repository.CustomerRepository;
import org.vl4ds4m.banking.repository.entity.CustomerRe;
import org.vl4ds4m.banking.service.expection.DuplicateEntityException;
import org.vl4ds4m.banking.service.expection.EntityNotFoundException;
import org.vl4ds4m.banking.service.expection.ServiceException;

import java.time.LocalDate;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    private final ConverterService converterService;

    public Customer getCustomerByName(String name) {
        return getCustomerRe(name).toEntity();
    }

    public void createCustomer(CreateCustomerRequest request) {
        customerRepository.findByName(request.getCustomerName()).ifPresent(c -> {
            var customerStr = Customer.logStr(request.getCustomerName());
            throw new DuplicateEntityException(customerStr);
        });

        LocalDate now = LocalDate.now();
        LocalDate maxBirthDate = now.minusYears(14);
        LocalDate minBirthDate = now.minusYears(121).plusDays(1);
        LocalDate birthDate = request.getBirthDate();

        if (birthDate.isAfter(maxBirthDate) || birthDate.isBefore(minBirthDate)) {
            throw new ServiceException("Customer age must be in range of 14 to 120 years. " +
                    "Passed birth date = " + birthDate);
        }

        var name = request.getCustomerName();
        var customer = new CustomerRe();
        customer.setName(name);
        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setBirthDate(birthDate);
        customer = customerRepository.save(customer);

        name = customer.getName();
        log.info("{} created", Customer.logStr(name));
    }

    public BalanceResponse getCustomerBalance(String customerName, Currency currency) {
        var customer = getCustomerRe(customerName);

        var accounts = customer.getAccounts();
        var balance = Money.empty();
        var totalCurrency = org.vl4ds4m.banking.entity.Currency.valueOf(currency);

        for (var account : accounts) {
            var money = Money.of(account.getAmount());

            if (money.isEmpty()) continue;

            Money converted = money;
            if (!account.getCurrency().equals(totalCurrency)) {
                converted = converterService.convert(account.getCurrency(), totalCurrency, money);
            }

            balance = balance.add(converted);
        }

        return new BalanceResponse(currency, balance.amount());
    }

    private CustomerRe getCustomerRe(String name) {
        return customerRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException(Customer.logStr(name)));
    }
}
