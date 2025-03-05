package edu.vl4ds4m.banking.service;

import edu.vl4ds4m.banking.exception.InvalidDataException;
import edu.vl4ds4m.banking.dao.AccountRepository;
import edu.vl4ds4m.banking.dto.*;
import edu.vl4ds4m.banking.exception.*;
import edu.vl4ds4m.banking.util.Conversions;
import io.micrometer.observation.annotation.Observed;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Validated
public class AccountService {
    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);

    private final AccountRepository accountRepository;
    private final CustomerService customerService;
    private final NotificationService notificationService;
    private final SimpMessagingService simpMessagingService;
    private final TransactionService transactionService;

    public AccountService(
            AccountRepository accountRepository,
            CustomerService customerService,
            NotificationService notificationService,
            SimpMessagingService simpMessagingService,
            TransactionService transactionService
    ) {
        this.accountRepository = accountRepository;
        this.customerService = customerService;
        this.notificationService = notificationService;
        this.simpMessagingService = simpMessagingService;
        this.transactionService = transactionService;
    }

    @Observed
    public AccountCreationResponse createAccount(@Valid AccountCreationRequest request) {
        Optional<Customer> customer = customerService.findById(request.customerId());
        if (customer.isEmpty()) {
            throw new InvalidCustomerIdException(request.customerId());
        }
        Optional<Account> account = accountRepository.findByCustomerIdAndCurrency(
                request.customerId(),
                request.currency());
        if (account.isPresent()) {
            throw new InvalidDataException(
                    "Account[customerId=" + request.customerId() + ", " +
                    "currency=" + request.currency() + "] already exists");
        }
        Account savedAccount = accountRepository.save(new Account(customer.get(), request.currency()));
        logger.debug("Create Account[number={}]", savedAccount.getNumber());
        simpMessagingService.sendMessage(savedAccount);
        return new AccountCreationResponse(savedAccount.getNumber());
    }

    @Observed
    public AccountBalance getBalance(int number) {
        Optional<Account> account = accountRepository.findById(number);
        if (account.isEmpty()) {
            throw new InvalidAccountNumberException(number);
        }
        logger.debug("Return Account[number={}] balance", account.get().getNumber());
        BigDecimal amount = Conversions.setScale(account.get().getAmount());
        Currency currency = account.get().getCurrency();
        return new AccountBalance(amount, currency);
    }

    @Observed
    @Transactional
    public TransactionResponse topUpAccount(int number, @Valid AccountTopUpRequest request) {
        Optional<Account> optionalAccount = accountRepository.findById(number);
        if (optionalAccount.isEmpty()) {
            throw new InvalidAccountNumberException(number);
        }
        BigDecimal amount = Conversions.setScale(request.amount());
        Account account = optionalAccount.get();
        account.setAmount(account.getAmount() + amount.doubleValue());
        account = accountRepository.save(account);
        logger.debug(
                "Top up Account[number={}] with {} {}",
                account.getNumber(), amount, account.getCurrency());
        BigDecimal accountAmount = Conversions.setScale(account.getAmount());
        notificationService.save(
                account.getCustomer().getId(),
                account.getNumber(),
                amount,
                accountAmount);
        simpMessagingService.sendMessage(account);
        Transaction transaction = transactionService.persist(
                new Transaction(account, amount.doubleValue()));
        return new TransactionResponse(transaction.getId(), amount);
    }

    @Observed
    public List<TransactionResponse> getTransactions(int number) {
        List<TransactionResponse> responses = accountRepository.findById(number)
                .map(Account::getTransactions)
                .orElseThrow(() -> new InvalidAccountNumberException(number))
                .stream().map(
                        t -> new TransactionResponse(t.getId(), Conversions.setScale(t.getAmount()))
                ).toList();
        logger.debug("Return Account[number={}] transactions", number);
        return responses;
    }
}
