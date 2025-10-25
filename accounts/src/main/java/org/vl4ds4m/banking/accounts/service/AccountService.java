package org.vl4ds4m.banking.accounts.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.vl4ds4m.banking.accounts.api.model.*;
import org.vl4ds4m.banking.accounts.api.util.CurrencyConverter;
import org.vl4ds4m.banking.accounts.entity.Account;
import org.vl4ds4m.banking.accounts.entity.Customer;
import org.vl4ds4m.banking.common.entity.Money;
import org.vl4ds4m.banking.accounts.repository.AccountRepository;
import org.vl4ds4m.banking.accounts.repository.CustomerRepository;
import org.vl4ds4m.banking.accounts.repository.entity.AccountRe;
import org.vl4ds4m.banking.accounts.service.expection.DuplicateEntityException;
import org.vl4ds4m.banking.accounts.service.expection.EntityNotFoundException;

import java.math.BigDecimal;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    private final CustomerRepository customerRepository;

    public Account getAccountByNumber(long number) {
        return getAccountRe(number).toEntity();
    }

    public CreateAccountResponse createAccount(CreateAccountRequest request) {
        var customerName = request.getCustomerName();
        var customer = customerRepository.findByName(customerName)
                .orElseThrow(() -> new EntityNotFoundException(Customer.logStr(customerName)));

        var currency = CurrencyConverter.toEntity(request.getCurrency());
        customer.getAccounts().stream()
                .filter(a -> a.getCurrency().equals(currency))
                .findAny()
                .ifPresent(a -> {
                    throw new DuplicateEntityException(
                            Account.logStr(customerName, currency));
                });

        var account = new AccountRe();
        account.setNumber(0L);
        account.setCustomer(customer);
        account.setCurrency(currency);
        account.setAmount(Money.empty().amount());
        account = accountRepository.save(account);

        long number = generateAccountNumber(account.getId());
        account.setNumber(number);

        account = accountRepository.save(account);
        number = account.getNumber();
        log.info("{} created", Account.logStr(number));

        return new CreateAccountResponse(number);
    }

    public BalanceResponse getAccountBalance(long number) {
        var account = getAccountRe(number);
        return new BalanceResponse(
                CurrencyConverter.toApi(account.getCurrency()),
                account.getAmount());
    }

    public AccountOperationResponse topUpAccount(long number, TopUpAccountRequest request) {
        var account = getAccountRe(number);

        var money = Money.of(account.getAmount())
                .add(Money.of(request.getAugend()));
        account.setAmount(money.amount());

        account = accountRepository.save(account);
        log.info("{} top up by {} {}",
                Account.logStr(account.getNumber()),
                request.getAugend(),
                account.getCurrency());

        return new AccountOperationResponse(
                CurrencyConverter.toApi(account.getCurrency()),
                account.getAmount());
    }

    public AccountOperationResponse withdrawMoneyToAccount(long number, WithdrawAccountRequest request) {
        var account = getAccountRe(number);

        var money = Money.of(account.getAmount())
                .subtract(Money.of(request.getSubtrahend()));
        account.setAmount(money.amount());

        account = accountRepository.save(account);
        log.info("{} withdraw {} {}",
                Account.logStr(account.getNumber()),
                request.getSubtrahend(),
                account.getCurrency());

        return new AccountOperationResponse(
                CurrencyConverter.toApi(account.getCurrency()),
                account.getAmount());
    }

    // ToDo implement proper transfer
    public void transferMoney(long senderNumber, long receiverNumber, BigDecimal amount) {
        final var money = Money.of(amount);
        var sender = getAccountRe(senderNumber);
        var receiver = getAccountRe(receiverNumber);

        final var senderMoneyBefore = Money.of(sender.getAmount());
        final var senderMoneyAfter = senderMoneyBefore.subtract(money);
        sender.setAmount(senderMoneyAfter.amount());

        final var receiverMoneyBefore = Money.of(receiver.getAmount());
        final var receiverMoneyAfter = receiverMoneyBefore.add(money);
        receiver.setAmount(receiverMoneyAfter.amount());

        accountRepository.save(sender);
        accountRepository.save(receiver);
    }

    private AccountRe getAccountRe(long number) {
        return accountRepository.findByNumber(number)
                .orElseThrow(() -> new EntityNotFoundException(Account.logStr(number)));
    }

    // ToDo implement proper generator
    private static long generateAccountNumber(long id) {
        long number = id + 1_000_000_000L;
        if (number <= 0L) {
            throw new RuntimeException("New account number must be positive. " +
                    "Generated value = " + number);
        }
        return number;
    }
}
