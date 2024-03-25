package edu.tinkoff.service;

import edu.tinkoff.dao.CustomerRepository;
import edu.tinkoff.model.*;
import edu.tinkoff.util.Conversions;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final ConverterService converterService;

    public CustomerService(CustomerRepository customerRepository, ConverterService converterService) {
        this.customerRepository = customerRepository;
        this.converterService = converterService;
    }

    public Optional<Customer> createCustomer(Customer customer) {
        if (
                customer.getFirstName() == null ||
                customer.getLastName() == null ||
                customer.getBirthDate() == null
        ) {
            return Optional.empty();
        }

        int roughCustomerAge = LocalDate.now().getYear() - customer.getBirthDate().getYear();
        if (roughCustomerAge < 14 || roughCustomerAge > 120) {
            return Optional.empty();
        }

        Customer savedCustomer = customerRepository.save(customer);

        customer = new Customer();
        customer.setId(savedCustomer.getId());
        return Optional.of(customer);
    }

    public Optional<Customer> findById(int id) {
        return customerRepository.findById(id);
    }

    public Optional<CustomerBalance> getBalance(int id, Currency currency) {
        Optional<Customer> customer = customerRepository.findById(id);
        if (customer.isEmpty()) {
            return Optional.empty();
        }

        Set<Account> accounts = customer.get().getAccounts();
        BigDecimal balance = Conversions.setScale(BigDecimal.ZERO);

        for (Account account : accounts) {
            BigDecimal amount = account.getAmount();
            if (BigDecimal.ZERO.compareTo(amount) < 0) {
                BigDecimal convertedAmount = converterService.convert(
                        account.getCurrency(),
                        currency,
                        amount
                );
                balance = balance.add(convertedAmount);
            }
        }

        return Optional.of(new CustomerBalance(balance, currency));
    }
}
