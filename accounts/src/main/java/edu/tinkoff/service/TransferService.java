package edu.tinkoff.service;

import edu.tinkoff.dao.AccountRepository;
import edu.tinkoff.dao.TransactionRepository;
import edu.tinkoff.dto.*;
import edu.tinkoff.exception.InvalidAccountNumberException;
import edu.tinkoff.exception.InvalidDataException;
import edu.tinkoff.util.Conversions;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@Validated
public class TransferService {
    private static final Logger log = LoggerFactory.getLogger(TransferService.class);

    private final ConverterService converterService;
    private final AccountRepository accountRepository;
    private final NotificationService notificationService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final TransactionRepository transactionRepository;
    private final AdminService adminService;

    public TransferService(
            ConverterService converterService,
            AccountRepository accountRepository,
            NotificationService notificationService,
            SimpMessagingTemplate simpMessagingTemplate,
            TransactionRepository transactionRepository,
            AdminService adminService
    ) {
        this.converterService = converterService;
        this.accountRepository = accountRepository;
        this.notificationService = notificationService;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.transactionRepository = transactionRepository;
        this.adminService = adminService;
    }

    @Transactional
    public TransactionResponse transfer(@Valid TransferRequest request) {
        Optional<Account> optionalReceiver = accountRepository.findById(request.receiverNumber());
        if (optionalReceiver.isEmpty()) {
            throw new InvalidAccountNumberException(request.receiverNumber());
        }

        Optional<Account> optionalSender = accountRepository.findById(request.senderNumber());
        if (optionalSender.isEmpty()) {
            throw new InvalidAccountNumberException(request.senderNumber());
        }

        Account receiver = optionalReceiver.get();
        Account sender = optionalSender.get();
        BigDecimal amount = Conversions.setScale(request.amount());
        BigDecimal senderAmount = Conversions.setScale(sender.getAmount());

        if (senderAmount.compareTo(amount) < 0) {
            throw new InvalidDataException(
                    "Insufficient funds in the sender account: " +
                    "amount=" + senderAmount);
        }

        return transfer(sender, receiver, amount);
    }

    private TransactionResponse transfer(Account sender, Account receiver, BigDecimal amount) {
        Currency senderCurrency = sender.getCurrency();
        Currency receiverCurrency = receiver.getCurrency();

        BigDecimal feeRate = adminService.getFee();
        BigDecimal transferredAmount = amount.subtract(amount.multiply(feeRate));

        BigDecimal convertedAmount = transferredAmount;
        if (!senderCurrency.equals(receiverCurrency)) {
            double converted = converterService.convert(
                    senderCurrency, receiverCurrency, transferredAmount.doubleValue());
            convertedAmount = Conversions.setScale(converted);
        }

        sender.setAmount(sender.getAmount() - amount.doubleValue());
        receiver.setAmount(receiver.getAmount() + convertedAmount.doubleValue());

        sender = accountRepository.save(sender);
        receiver = accountRepository.save(receiver);
        log.info("Transfer currency from Account[{}] to Account[{}]",
                sender.getNumber(), receiver.getNumber());

        BigDecimal negatedAmount = amount.negate();
        Transaction senderTransaction = persistTransaction(
                new Transaction(sender, negatedAmount.doubleValue()));
        persistTransaction(new Transaction(receiver, convertedAmount.doubleValue()));

        notificationService.save(
                sender.getCustomer().getId(),
                sender.getNumber(),
                negatedAmount,
                Conversions.setScale(sender.getAmount()));
        notificationService.save(
                receiver.getCustomer().getId(),
                receiver.getNumber(),
                convertedAmount,
                Conversions.setScale(receiver.getAmount()));

        sendMessage(sender);
        sendMessage(receiver);

        return new TransactionResponse(senderTransaction.getId(), negatedAmount);
    }

    private Transaction persistTransaction(Transaction transaction) {
        transaction = transactionRepository.save(transaction);
        log.info("Persist Transaction[{}]", transaction.getId());
        return transaction;
    }

    private void sendMessage(Account account) {
        AccountBrokerMessage message = new AccountBrokerMessage(
                account.getNumber(),
                account.getCurrency(),
                Conversions.setScale(account.getAmount())
        );
        simpMessagingTemplate.convertAndSend(AccountBrokerMessage.DESTINATION, message);
        log.info("Send {}", message);
    }
}
