package edu.tinkoff.service;

import edu.tinkoff.dao.AccountRepository;
import edu.tinkoff.model.Account;
import edu.tinkoff.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    public Account createAccount(Customer customer, String currency) {
        Account account = new Account(customer, currency);
        return accountRepository.save(account);
    }

    public BigDecimal getBalance(Account account) {
        return BigDecimal.valueOf(account.getAmount())
                .setScale(2, RoundingMode.HALF_EVEN);
    }

    public void topUpAccount(Account account, double amount) {
        account.setAmount(account.getAmount() + amount);
        accountRepository.save(account);
    }
}
