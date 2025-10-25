package org.vl4ds4m.banking.accounts.transfer;

import io.micrometer.observation.annotation.Observed;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.vl4ds4m.banking.common.Conversions;
import org.vl4ds4m.banking.accounts.account.Account;
import org.vl4ds4m.banking.accounts.account.AccountRepository;
import org.vl4ds4m.banking.accounts.admin.AdminService;
import org.vl4ds4m.banking.accounts.api.model.Currency;
import org.vl4ds4m.banking.accounts.converter.ConverterService;
import org.vl4ds4m.banking.accounts.exception.InvalidAccountNumberException;
import org.vl4ds4m.banking.accounts.exception.InvalidDataException;
import org.vl4ds4m.banking.accounts.messaging.SimpMessagingService;
import org.vl4ds4m.banking.accounts.notification.NotificationService;
import org.vl4ds4m.banking.accounts.transaction.Transaction;
import org.vl4ds4m.banking.accounts.transaction.TransactionResponse;
import org.vl4ds4m.banking.accounts.transaction.TransactionService;

import java.math.BigDecimal;

// @Service
@Validated
public class TransferService {
    private static final Logger logger = LoggerFactory.getLogger(TransferService.class);

    private final ConverterService converterService;
    private final AccountRepository accountRepository;
    private final NotificationService notificationService;
    private final SimpMessagingService simpMessagingService;
    private final TransactionService transactionService;
    private final AdminService adminService;

    public TransferService(
        ConverterService converterService,
        AccountRepository accountRepository,
        NotificationService notificationService,
        SimpMessagingService simpMessagingService,
        TransactionService transactionService,
        AdminService adminService
    ) {
        this.converterService = converterService;
        this.accountRepository = accountRepository;
        this.notificationService = notificationService;
        this.simpMessagingService = simpMessagingService;
        this.transactionService = transactionService;
        this.adminService = adminService;
    }

    @Observed
    @Transactional
    public TransactionResponse transfer(@Valid TransferRequest request) {
        Account receiver = accountRepository.findById(request.receiverNumber())
            .orElseThrow(() -> new InvalidAccountNumberException(request.receiverNumber()));
        Account sender = accountRepository.findById(request.senderNumber())
            .orElseThrow(() -> new InvalidAccountNumberException(request.senderNumber()));

        BigDecimal amount = Conversions.setScale(request.amount());
        BigDecimal senderAmount = Conversions.setScale(sender.getAmount());

        if (senderAmount.compareTo(amount) < 0) {
            throw new InvalidDataException(
                "Insufficient funds in the sender account: amount=" + senderAmount);
        }

        return transfer(sender, receiver, amount);
    }

    private TransactionResponse transfer(Account sender, Account receiver, BigDecimal amount) {
        Currency senderCurrency = null;
        Currency receiverCurrency = null;

        BigDecimal feeRate = adminService.getFee();
        BigDecimal transferredAmount = amount.subtract(amount.multiply(feeRate));

        BigDecimal convertedAmount = transferredAmount;
        if (!senderCurrency.equals(receiverCurrency)) {
            convertedAmount = converterService.convert(
                senderCurrency, receiverCurrency, transferredAmount);
        }

        sender.setAmount(sender.getAmount().subtract(amount));
        receiver.setAmount(receiver.getAmount().add(convertedAmount));
        sender = accountRepository.save(sender);
        receiver = accountRepository.save(receiver);
        logger.debug("Transfer {} {} from Account[number={}] to Account[number={}]",
            amount, senderCurrency, sender.getNumber(), receiver.getNumber());

        BigDecimal negatedAmount = amount.negate();
        Transaction senderTransaction = transactionService.persist(
            new Transaction(sender, negatedAmount));
        transactionService.persist(
            new Transaction(receiver, convertedAmount));

        notificationService.save(
            sender.getCustomer().getId(),
            sender.getNumber(),
            negatedAmount,
            sender.getAmount());
        notificationService.save(
            receiver.getCustomer().getId(),
            receiver.getNumber(),
            convertedAmount,
            receiver.getAmount());

        simpMessagingService.sendMessage(null); //sender
        simpMessagingService.sendMessage(null); //receiver

        return new TransactionResponse(senderTransaction.getId(), negatedAmount);
    }
}
