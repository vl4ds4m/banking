package edu.tinkoff.service;

import edu.tinkoff.dao.AccountRepository;
import edu.tinkoff.dto.*;
import edu.tinkoff.exception.InvalidDataException;
import edu.tinkoff.util.Conversions;
import jakarta.validation.Valid;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@Validated
public class AccountService {
    private final AccountRepository accountRepository;
    private final CustomerService customerService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public AccountService(
            AccountRepository accountRepository,
            CustomerService customerService,
            SimpMessagingTemplate simpMessagingTemplate
    ) {
        this.accountRepository = accountRepository;
        this.customerService = customerService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    public AccountCreationResponse createAccount(@Valid AccountCreationRequest request) {
        Optional<Customer> customer = customerService.findById(request.customerId());
        if (customer.isEmpty()) {
            throw new InvalidDataException("Customer [id=" + request.customerId() + "] isn't found");
        }

        Optional<Account> account = accountRepository.findByCustomerIdAndCurrency(
                request.customerId(),
                request.currency()
        );
        if (account.isPresent()) {
            throw new InvalidDataException(
                    "Account [customerId=" + request.customerId() + ", " +
                    "currency=" + request.currency() + "] already exists");
        }

        Account savedAccount = accountRepository.save(new Account(customer.get(), request.currency()));

        AccountBrokerMessage brokerMessage = new AccountBrokerMessage(
                savedAccount.getNumber(),
                request.currency(),
                Conversions.setScale(savedAccount.getAmount())
        );
        simpMessagingTemplate.convertAndSend(AccountBrokerMessage.DESTINATION, brokerMessage);

        return new AccountCreationResponse(savedAccount.getNumber());
    }

    public AccountBalance getBalance(int number) {
        Optional<Account> account = accountRepository.findById(number);
        if (account.isEmpty()) {
            throw new InvalidDataException("Account [number=" + number + "] isn't found");
        }

        BigDecimal amount = Conversions.setScale(account.get().getAmount());
        Currency currency = account.get().getCurrency();
        return new AccountBalance(amount, currency);
    }

    public void topUpAccount(int number, @Valid AccountTopUpRequest request) {
        Optional<Account> optionalAccount = accountRepository.findById(number);
        if (optionalAccount.isEmpty()) {
            throw new InvalidDataException("Account [number=" + number + "] isn't found");
        }

        BigDecimal amount = Conversions.setScale(request.amount());
        Account account = optionalAccount.get();
        account.setAmount(account.getAmount().add(amount));

        account = accountRepository.save(account);

        AccountBrokerMessage brokerMessage = new AccountBrokerMessage(
                account.getNumber(),
                account.getCurrency(),
                Conversions.setScale(account.getAmount())
        );
        simpMessagingTemplate.convertAndSend(AccountBrokerMessage.DESTINATION, brokerMessage);
    }

    public Optional<Account> findById(int id) {
        return accountRepository.findById(id);
    }

    public Account save(Account account) {
        return accountRepository.save(account);
    }
}
