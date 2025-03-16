package edu.vl4ds4m.banking.transfer;

import edu.vl4ds4m.banking.account.AccountRepository;
import edu.vl4ds4m.banking.account.Account;
import edu.vl4ds4m.banking.admin.AdminService;
import edu.vl4ds4m.banking.converter.ConverterService;
import edu.vl4ds4m.banking.notification.NotificationService;
import edu.vl4ds4m.banking.messaging.SimpMessagingService;
import edu.vl4ds4m.banking.transaction.Transaction;
import edu.vl4ds4m.banking.dto.TransactionResponse;
import edu.vl4ds4m.banking.dto.TransferRequest;
import edu.vl4ds4m.banking.exception.InvalidAccountNumberException;
import edu.vl4ds4m.banking.dto.*;
import edu.vl4ds4m.banking.exception.InvalidDataException;
import edu.vl4ds4m.banking.Conversions;
import edu.vl4ds4m.banking.transaction.TransactionService;
import io.micrometer.observation.annotation.Observed;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;

@Service
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
        Currency senderCurrency = sender.getCurrency();
        Currency receiverCurrency = receiver.getCurrency();

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

        simpMessagingService.sendMessage(sender);
        simpMessagingService.sendMessage(receiver);

        return new TransactionResponse(senderTransaction.getId(), negatedAmount);
    }
}
