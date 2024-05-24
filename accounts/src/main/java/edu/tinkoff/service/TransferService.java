package edu.tinkoff.service;

import edu.tinkoff.dao.TransactionRepository;
import edu.tinkoff.dto.*;
import edu.tinkoff.exception.InvalidAccountNumberException;
import edu.tinkoff.exception.InvalidDataException;
import edu.tinkoff.util.Conversions;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@Validated
public class TransferService {
    private final ConverterService converterService;
    private final AccountService accountService;
    private final NotificationService notificationService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final TransactionRepository transactionRepository;
    private final AdminService adminService;

    public TransferService(
            ConverterService converterService,
            AccountService accountService,
            NotificationService notificationService,
            SimpMessagingTemplate simpMessagingTemplate,
            TransactionRepository transactionRepository,
            AdminService adminService
    ) {
        this.converterService = converterService;
        this.accountService = accountService;
        this.notificationService = notificationService;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.transactionRepository = transactionRepository;
        this.adminService = adminService;
    }

    @Transactional
    public TransactionResponse transfer(@Valid TransferRequest request) {
        Optional<Account> optionalReceiver = accountService.findById(request.receiverNumber());
        if (optionalReceiver.isEmpty()) {
            throw new InvalidAccountNumberException(request.receiverNumber());
        }

        Optional<Account> optionalSender = accountService.findById(request.senderNumber());
        if (optionalSender.isEmpty()) {
            throw new InvalidAccountNumberException(request.senderNumber());
        }

        Account receiver = optionalReceiver.get();
        Account sender = optionalSender.get();
        BigDecimal amount = Conversions.setScale(request.amount());

        if (sender.getAmount().compareTo(amount) < 0) {
            throw new InvalidDataException(
                    "Insufficient funds in the sender account: " +
                    "amount=" + sender.getAmount());
        }

        return transfer(sender, receiver, amount);
    }

    private TransactionResponse transfer(Account sender, Account receiver, BigDecimal amount) {
        Currency senderCurrency = sender.getCurrency();
        Currency receiverCurrency = receiver.getCurrency();

        BigDecimal feeRate = adminService.getFee();
        BigDecimal transferredAmount = amount.subtract(amount.multiply(feeRate));

        BigDecimal convertedAmount = !senderCurrency.equals(receiverCurrency) ?
                converterService.convert(senderCurrency, receiverCurrency, transferredAmount) :
                transferredAmount;

        sender.setAmount(sender.getAmount().subtract(amount));
        receiver.setAmount(receiver.getAmount().add(convertedAmount));

        sender = accountService.save(sender);
        receiver = accountService.save(receiver);

        Transaction senderTransaction = transactionRepository.save(new Transaction(sender, amount.negate()));
        transactionRepository.save(new Transaction(receiver, convertedAmount));

        notificationService.save(
                sender.getCustomer().getId(),
                sender.getNumber(),
                amount.negate(),
                sender.getAmount());
        notificationService.save(
                receiver.getCustomer().getId(),
                receiver.getNumber(),
                convertedAmount,
                receiver.getAmount());

        sendMessage(sender);
        sendMessage(receiver);

        return new TransactionResponse(senderTransaction.getId(), senderTransaction.getAmount());
    }

    private void sendMessage(Account account) {
        AccountBrokerMessage message = new AccountBrokerMessage(
                account.getNumber(),
                account.getCurrency(),
                Conversions.setScale(account.getAmount())
        );
        simpMessagingTemplate.convertAndSend(AccountBrokerMessage.DESTINATION, message);
    }
}
