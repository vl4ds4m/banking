package org.vl4ds4m.banking.accounts.service;

import lombok.RequiredArgsConstructor;
import org.vl4ds4m.banking.accounts.model.Account;
import org.vl4ds4m.banking.accounts.model.Currency;
import org.vl4ds4m.banking.accounts.model.Money;
import org.vl4ds4m.banking.accounts.repository.AccountRepository;
import org.vl4ds4m.banking.accounts.repository.CustomerRepository;
import org.vl4ds4m.banking.accounts.repository.model.AccountPe;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    private final CustomerRepository customerRepository;

    public Account createAccount(String customerName, Currency currency) {
        var customer = customerRepository.findByName(customerName)
                .orElseThrow(() -> new ServiceException("Customer[name=" + customerName + "] not found"));

        boolean existed = customer.getAccounts().stream()
                .anyMatch(a -> a.getCurrency().equals(currency));
        if (existed) {
            var message = "Account[customerName=" + customerName + ",currency=" + currency + "] already existed";
            throw new ServiceException(message);
        }

        var account = new AccountPe(customer, currency, Money.ZERO.amount());
        account = accountRepository.save(account);

        return new Account(account.getNumber(), currency, Money.ZERO);
    }

    public Account getAccountByNumber(Long number) {
        var account = accountRepository.findById(number)
                .orElseThrow(() -> accountNotFound(number));
        return fromPe(account);
    }

    public void topUpAccount(Long number, BigDecimal augend) {
        var account = accountRepository.findById(number)
                .orElseThrow(() -> accountNotFound(number));
        var money = new Money(account.getAmount())
                .add(new Money(augend));
        account.setAmount(money.amount());
        accountRepository.save(account);
    }

    public void withdrawMoneyToAccount(Long number, BigDecimal subtrahend) {
        var account = accountRepository.findById(number)
                .orElseThrow(() -> accountNotFound(number));
        var money = new Money(account.getAmount())
                .subtract(new Money(subtrahend));
        account.setAmount(money.amount());
        accountRepository.save(account);
    }

    private static Account fromPe(AccountPe pe) {
        return new Account(pe.getNumber(), pe.getCurrency(), new Money(pe.getAmount()));
    }

    private static ServiceException accountNotFound(Long number) {
        return new ServiceException("Account[number=" + number + "] not found");
    }
}
