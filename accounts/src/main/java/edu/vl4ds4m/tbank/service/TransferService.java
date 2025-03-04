package edu.vl4ds4m.tbank.service;

import edu.vl4ds4m.tbank.dao.AccountRepository;
import edu.vl4ds4m.tbank.dto.*;
import edu.vl4ds4m.tbank.exception.InvalidAccountNumberException;
import edu.vl4ds4m.tbank.exception.InvalidDataException;
import edu.vl4ds4m.tbank.util.Conversions;
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
        logger.debug("Transfer {} {} from Account[number={}] to Account[number={}]",
                amount, senderCurrency, sender.getNumber(), receiver.getNumber());
        BigDecimal negatedAmount = amount.negate();
        Transaction senderTransaction = transactionService.persist(
                new Transaction(sender, negatedAmount.doubleValue()));
        transactionService.persist(
                new Transaction(receiver, convertedAmount.doubleValue()));
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
        simpMessagingService.sendMessage(sender);
        simpMessagingService.sendMessage(receiver);
        return new TransactionResponse(senderTransaction.getId(), negatedAmount);
    }
}
