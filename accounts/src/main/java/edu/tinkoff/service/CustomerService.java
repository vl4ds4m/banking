package edu.tinkoff.service;

import edu.tinkoff.dao.AccountRepository;
import edu.tinkoff.dao.CustomerRepository;
import edu.tinkoff.model.Account;
import edu.tinkoff.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ConverterService converterService;

    public Customer createCustomer(String firstName, String lastName, LocalDate birthDate) {
        Customer customer = new Customer(firstName, lastName, birthDate);
        return customerRepository.save(customer);
    }

    public BigDecimal getBalance(int customerId, String currency) {
        List<Account> accounts = accountRepository.findAllByCustomerId(customerId);

        BigDecimal balance = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_EVEN);

        for (Account account : accounts) {
            if (account.getAmount() > 0) {
                Map<String, Object> responseBody = converterService.convert(
                        account.getCurrency(),
                        currency,
                        account.getAmount()
                );
                BigDecimal amount = BigDecimal.valueOf((double) responseBody.get("amount"));
                balance = balance.add(amount);
            }
        }

        return balance;
    }
}
