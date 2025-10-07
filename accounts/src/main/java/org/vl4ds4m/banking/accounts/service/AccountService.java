package org.vl4ds4m.banking.accounts.service;

import lombok.RequiredArgsConstructor;
import org.vl4ds4m.banking.accounts.model.Account;
import org.vl4ds4m.banking.accounts.model.Currency;
import org.vl4ds4m.banking.accounts.model.Money;
import org.vl4ds4m.banking.accounts.repository.AccountRepository;
import org.vl4ds4m.banking.accounts.repository.CustomerRepository;
import org.vl4ds4m.banking.accounts.repository.model.AccountPe;

@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    private final CustomerRepository customerRepository;

    public Account createAccount(String customerName, Currency currency) {
        var customer = customerRepository.findByName(customerName)
                .orElseThrow(() -> new RuntimeException("Customer[name=" + customerName + "] not found"));

        boolean existed = customer.getAccounts().stream()
                .anyMatch(a -> a.getCurrency().equals(currency));
        if (existed) {
            var message = "Account[customerName=" + customerName + ",currency=" + currency + "] already existed";
            throw new RuntimeException(message);
        }

        var account = new AccountPe(customer, currency, Money.ZERO.amount());
        account = accountRepository.save(account);

        return new Account(account.getNumber(), currency, Money.ZERO);
    }

    public Account getAccountByNumber(Long number) {
        var account = accountRepository.findById(number)
                .orElseThrow(() -> new RuntimeException("Account[number=" + number + "] not found"));
        return new Account(account.getNumber(), account.getCurrency(), new Money(account.getAmount()));
    }
}
