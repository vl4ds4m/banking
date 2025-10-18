package org.vl4ds4m.banking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.vl4ds4m.banking.entity.Account;
import org.vl4ds4m.banking.entity.Currency;
import org.vl4ds4m.banking.entity.Money;
import org.vl4ds4m.banking.repository.AccountRepository;
import org.vl4ds4m.banking.repository.CustomerRepository;
import org.vl4ds4m.banking.repository.entity.AccountRe;

import java.math.BigDecimal;

@Service
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

        var account = new AccountRe(customer, currency, Money.ZERO.amount());
        account = accountRepository.save(account);

        return new Account(account.getNumber(), currency, Money.ZERO);
    }

    public Account getAccountByNumber(Long number) {
        var account = getAccount(number);
        return fromRe(account);
    }

    public void topUpAccount(Long number, BigDecimal augend) {
        var account = getAccount(number);
        var money = new Money(account.getAmount())
                .add(new Money(augend));
        account.setAmount(money.amount());
        accountRepository.save(account);
    }

    public void withdrawMoneyToAccount(Long number, BigDecimal subtrahend) {
        var account = getAccount(number);
        var money = new Money(account.getAmount())
                .subtract(new Money(subtrahend));
        account.setAmount(money.amount());
        accountRepository.save(account);
    }

    public void transferMoney(Long senderNumber, Long receiverNumber, BigDecimal amount) {
        final var money = new Money(amount);
        var sender = getAccount(senderNumber);
        var receiver = getAccount(receiverNumber);

        final var senderMoneyBefore = new Money(sender.getAmount());
        final var senderMoneyAfter = senderMoneyBefore.subtract(money);
        sender.setAmount(senderMoneyAfter.amount());

        final var receiverMoneyBefore = new Money(receiver.getAmount());
        final var receiverMoneyAfter = receiverMoneyBefore.add(money);
        receiver.setAmount(receiverMoneyAfter.amount());

        accountRepository.save(sender);
        accountRepository.save(receiver);
    }

    private AccountRe getAccount(Long number) {
        return accountRepository.findById(number)
                .orElseThrow(() -> new ServiceException("Account[number=" + number + "] not found"));
    }

    private static Account fromRe(AccountRe pe) {
        return new Account(pe.getNumber(), pe.getCurrency(), new Money(pe.getAmount()));
    }
}
