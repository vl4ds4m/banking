package edu.tinkoff.service;

import edu.tinkoff.dao.AccountRepository;
import edu.tinkoff.dao.TransactionRepository;
import edu.tinkoff.dto.*;
import edu.tinkoff.exception.*;
import edu.tinkoff.util.Conversions;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Validated
public class AccountService {
    private final AccountRepository accountRepository;
    private final CustomerService customerService;
    private final NotificationService notificationService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final TransactionRepository transactionRepository;

    public AccountService(
            AccountRepository accountRepository,
            CustomerService customerService,
            NotificationService notificationService,
            SimpMessagingTemplate simpMessagingTemplate,
            TransactionRepository transactionRepository
    ) {
        this.accountRepository = accountRepository;
        this.customerService = customerService;
        this.notificationService = notificationService;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.transactionRepository = transactionRepository;
    }

    public AccountCreationResponse createAccount(@Valid AccountCreationRequest request) {
        Optional<Customer> customer = customerService.findById(request.customerId());
        if (customer.isEmpty()) {
            throw new InvalidCustomerIdException(request.customerId());
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
            throw new InvalidAccountNumberException(number);
        }

        BigDecimal amount = Conversions.setScale(account.get().getAmount());
        Currency currency = account.get().getCurrency();
        return new AccountBalance(amount, currency);
    }

    @Transactional
    public TransactionResponse topUpAccount(int number, @Valid AccountTopUpRequest request) {
        Optional<Account> optionalAccount = accountRepository.findById(number);
        if (optionalAccount.isEmpty()) {
            throw new InvalidAccountNumberException(number);
        }

        BigDecimal amount = Conversions.setScale(request.amount());
        Account account = optionalAccount.get();
        account.setAmount(account.getAmount().add(amount));

        account = accountRepository.save(account);

        notificationService.save(
                account.getCustomer().getId(),
                account.getNumber(),
                amount,
                account.getAmount());

        AccountBrokerMessage brokerMessage = new AccountBrokerMessage(
                account.getNumber(),
                account.getCurrency(),
                Conversions.setScale(account.getAmount()));
        simpMessagingTemplate.convertAndSend(AccountBrokerMessage.DESTINATION, brokerMessage);

        Transaction transaction = transactionRepository.save(new Transaction(account, amount));
        return new TransactionResponse(transaction.getId(), transaction.getAmount());
    }

    public List<TransactionResponse> getTransactions(int number) {
        return accountRepository.findById(number)
                .map(Account::getTransactions)
                .orElseThrow(() -> new InvalidAccountNumberException(number))
                .stream().map(t -> new TransactionResponse(t.getId(), t.getAmount()))
                .toList();
    }

    public Optional<Account> findById(int id) {
        return accountRepository.findById(id);
    }

    public Account save(Account account) {
        return accountRepository.save(account);
    }
}
