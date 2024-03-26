package edu.tinkoff.service;

import edu.tinkoff.dao.AccountRepository;
import edu.tinkoff.dto.*;
import edu.tinkoff.util.Conversions;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final CustomerService customerService;

    public AccountService(AccountRepository accountRepository, CustomerService customerService) {
        this.accountRepository = accountRepository;
        this.customerService = customerService;
    }

    public Optional<AccountMessage> createAccount(AccountMessage message) {
        if (message.customerId() == null || message.currency() == null) {
            return Optional.empty();
        }

        Optional<Customer> customer = customerService.findById(message.customerId());
        if (customer.isEmpty()) {
            return Optional.empty();
        }

        Optional<Account> account = accountRepository.findByCustomerIdAndCurrency(
                message.customerId(),
                message.currency()
        );
        if (account.isPresent()) {
            return Optional.empty();
        }

        Account savedAccount = accountRepository.save(new Account(customer.get(), message.currency()));
        return Optional.of(new AccountMessage(null, null, savedAccount.getNumber()));
    }

    public Optional<AccountBalance> getBalance(int number) {
        Optional<Account> account = accountRepository.findById(number);
        if (account.isEmpty()) {
            return Optional.empty();
        }

        BigDecimal amount = Conversions.setScale(account.get().getAmount());
        Currency currency = account.get().getCurrency();
        return Optional.of(new AccountBalance(amount, currency));
    }

    public boolean topUpAccount(int number, AccountBalance balance) {
        Optional<Account> optionalAccount = accountRepository.findById(number);
        if (optionalAccount.isEmpty()) {
            return false;
        }

        BigDecimal amount = Conversions.setScale(balance.amount());
        if (BigDecimal.ZERO.compareTo(amount) >= 0) {
            return false;
        }

        Account account = optionalAccount.get();
        account.setAmount(account.getAmount().add(amount));
        accountRepository.save(account);

        return true;
    }

    public Optional<Account> findById(int id) {
        return accountRepository.findById(id);
    }

    public void save(Account account) {
        accountRepository.save(account);
    }
}
