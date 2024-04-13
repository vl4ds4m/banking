package edu.tinkoff.service;

import edu.tinkoff.dto.Account;
import edu.tinkoff.dto.AccountBrokerMessage;
import edu.tinkoff.dto.Currency;
import edu.tinkoff.dto.TransferRequest;
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

    public TransferService(
            ConverterService converterService,
            AccountService accountService,
            NotificationService notificationService,
            SimpMessagingTemplate simpMessagingTemplate
    ) {
        this.converterService = converterService;
        this.accountService = accountService;
        this.notificationService = notificationService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @Transactional
    public void transfer(@Valid TransferRequest request) {
        Optional<Account> optionalReceiver = accountService.findById(request.receiverNumber());
        if (optionalReceiver.isEmpty()) {
            throw new InvalidDataException(
                    "Account [number=" + request.receiverNumber() + "] isn't found");
        }

        Optional<Account> optionalSender = accountService.findById(request.senderNumber());
        if (optionalSender.isEmpty()) {
            throw new InvalidDataException(
                    "Account [number=" + request.senderNumber() + "] isn't found");
        }

        Account receiver = optionalReceiver.get();
        Account sender = optionalSender.get();
        BigDecimal amount = Conversions.setScale(request.amount());

        if (sender.getAmount().compareTo(amount) < 0) {
            throw new InvalidDataException(
                    "Insufficient funds in the sender account: " +
                    "amount=" + sender.getAmount());
        }

        transfer(sender, receiver, amount);
    }

    private void transfer(Account sender, Account receiver, BigDecimal amount) {
        Currency senderCurrency = sender.getCurrency();
        Currency receiverCurrency = receiver.getCurrency();

        BigDecimal convertedAmount = !senderCurrency.equals(receiverCurrency) ?
                converterService.convert(senderCurrency, receiverCurrency, amount) :
                amount;

        sender.setAmount(sender.getAmount().subtract(amount));
        receiver.setAmount(receiver.getAmount().add(convertedAmount));

        sender = accountService.save(sender);
        receiver = accountService.save(receiver);

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
