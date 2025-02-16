package edu.vl4ds4m.tbank.service;

import edu.vl4ds4m.tbank.dao.AccountRepository;
import edu.vl4ds4m.tbank.dao.TransactionRepository;
import edu.vl4ds4m.tbank.dto.*;
import edu.vl4ds4m.tbank.exception.*;
import edu.vl4ds4m.tbank.util.Conversions;
import io.micrometer.observation.annotation.Observed;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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

    @Observed
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
                    "Account[customerId=" + request.customerId() + ", " +
                    "currency=" + request.currency() + "] already exists");
        }

        Account savedAccount = accountRepository.save(new Account(customer.get(), request.currency()));
        logger.info("Create Account[{}]", savedAccount.getNumber());

        AccountBrokerMessage brokerMessage = new AccountBrokerMessage(
                savedAccount.getNumber(),
                request.currency(),
                Conversions.setScale(savedAccount.getAmount()));
        sendMessage(brokerMessage);

        return new AccountCreationResponse(savedAccount.getNumber());
    }

    private void sendMessage(AccountBrokerMessage message) {
        simpMessagingTemplate.convertAndSend(AccountBrokerMessage.DESTINATION, message);
        logger.info("Send {}", message);
    }

    @Observed
    public AccountBalance getBalance(int number) {
        Optional<Account> account = accountRepository.findById(number);
        if (account.isEmpty()) {
            throw new InvalidAccountNumberException(number);
        }

        logger.info("Return Account[{}] balance", account.get().getNumber());
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
        logger.info("Top up Account[{}]", account.getNumber());

        BigDecimal accountAmount = Conversions.setScale(account.getAmount());
        notificationService.save(
                account.getCustomer().getId(),
                account.getNumber(),
                amount,
                accountAmount);

        AccountBrokerMessage brokerMessage = new AccountBrokerMessage(
                account.getNumber(),
                account.getCurrency(),
                accountAmount);
        sendMessage(brokerMessage);

        Transaction transaction = transactionRepository.save(
                new Transaction(account, amount.doubleValue()));
        logger.info("Persist Transaction[{}]", transaction.getId());
        return new TransactionResponse(transaction.getId(), amount);
    }

    @Observed
    public List<TransactionResponse> getTransactions(int number) {
        logger.info("Return Account[{}] transactions", number);
        return accountRepository.findById(number)
                .map(Account::getTransactions)
                .orElseThrow(() -> new InvalidAccountNumberException(number))
                .stream().map(t -> new TransactionResponse(
                        t.getId(), Conversions.setScale(t.getAmount())))
                .toList();
    }
}
