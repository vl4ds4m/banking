package edu.vl4ds4m.banking.account;

import edu.vl4ds4m.banking.Conversions;
import edu.vl4ds4m.banking.account.dto.AccountBalance;
import edu.vl4ds4m.banking.account.dto.AccountCreationRequest;
import edu.vl4ds4m.banking.account.dto.AccountCreationResponse;
import edu.vl4ds4m.banking.account.dto.AccountTopUpRequest;
import edu.vl4ds4m.banking.exception.InvalidAccountNumberException;
import edu.vl4ds4m.banking.exception.InvalidCustomerIdException;
import edu.vl4ds4m.banking.notification.NotificationService;
import edu.vl4ds4m.banking.messaging.SimpMessagingService;
import edu.vl4ds4m.banking.transaction.Transaction;
import edu.vl4ds4m.banking.transaction.TransactionResponse;
import edu.vl4ds4m.banking.transaction.TransactionService;
import edu.vl4ds4m.banking.customer.Customer;
import edu.vl4ds4m.banking.customer.CustomerService;
import edu.vl4ds4m.banking.exception.InvalidDataException;
import io.micrometer.observation.annotation.Observed;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.List;

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
        Customer customer = customerService.findById(request.customerId())
            .orElseThrow(() -> new InvalidCustomerIdException(request.customerId()));
        accountRepository.findByCustomerIdAndCurrency(
            request.customerId(),
            request.currency()
        ).ifPresent(account -> {
            throw new InvalidDataException(
                "Account[customerId=" + request.customerId() + ", "
                    + "currency=" + request.currency() + "] already exists");
        });

        Account savedAccount = accountRepository.save(
            new Account(customer, request.currency()));
        logger.debug("Account[number={}] created", savedAccount.getNumber());

        simpMessagingService.sendMessage(savedAccount);

        return new AccountCreationResponse(savedAccount.getNumber());
    }

    @Observed
    public AccountBalance getBalance(int number) {
        Account account = accountRepository.findById(number)
            .orElseThrow(() -> new InvalidAccountNumberException(number));

        logger.debug("Return Account[number={}] balance", account.getNumber());
        return new AccountBalance(account.getAmount(), account.getCurrency());
    }

    @Observed
    @Transactional
    public TransactionResponse topUpAccount(int number, @Valid AccountTopUpRequest request) {
        Account account = accountRepository.findById(number)
            .orElseThrow(() -> new InvalidAccountNumberException(number));

        BigDecimal amount = Conversions.setScale(request.amount());
        BigDecimal old = account.getAmount();
        account.setAmount(old.add(amount));
        account = accountRepository.save(account);
        logger.debug(
            "Account[number={},currency={}]: {} -> {}",
            account.getNumber(), account.getCurrency(),
            old, account.getAmount());

        notificationService.save(
            account.getCustomer().getId(),
            account.getNumber(),
            amount,
            account.getAmount());

        simpMessagingService.sendMessage(account);

        Transaction transaction = transactionService.persist(
            new Transaction(account, amount));
        return new TransactionResponse(transaction.getId(), amount);
    }

    @Observed
    public List<TransactionResponse> getTransactions(int number) {
        Account account = accountRepository.findById(number)
            .orElseThrow(() -> new InvalidAccountNumberException(number));

        logger.debug("Return Account[number={}] transactions", number);
        return account.getTransactions().stream()
            .map(t -> new TransactionResponse(t.getId(), t.getAmount()))
            .toList();
    }
}
