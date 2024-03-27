package edu.tinkoff.service;

import edu.tinkoff.dto.Account;
import edu.tinkoff.dto.AccountBrokerMessage;
import edu.tinkoff.dto.Currency;
import edu.tinkoff.dto.TransferMessage;
import edu.tinkoff.util.Conversions;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class TransferService {
    private final ConverterService converterService;
    private final AccountService accountService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public TransferService(
            ConverterService converterService,
            AccountService accountService,
            SimpMessagingTemplate simpMessagingTemplate
    ) {
        this.converterService = converterService;
        this.accountService = accountService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    public boolean transfer(TransferMessage message) {
        if (
                message.receiverAccount() == null ||
                message.senderAccount() == null ||
                message.amountInSenderCurrency() == null
        ) {
            return false;
        }

        BigDecimal amount = Conversions.setScale(message.amountInSenderCurrency());
        if (BigDecimal.ZERO.compareTo(amount) >= 0) {
            return false;
        }

        Optional<Account> optionalReceiver = accountService.findById(message.receiverAccount());
        Optional<Account> optionalSender = accountService.findById(message.senderAccount());
        if (optionalReceiver.isEmpty() || optionalSender.isEmpty()) {
            return false;
        }

        Account receiver = optionalReceiver.get();
        Account sender = optionalSender.get();
        if (sender.getAmount().compareTo(amount) < 0) {
            return false;
        }

        Currency receiverCurrency = receiver.getCurrency();
        Currency senderCurrency = sender.getCurrency();
        BigDecimal convertedAmount = !senderCurrency.equals(receiverCurrency) ?
                converterService.convert(senderCurrency, receiverCurrency, amount) :
                amount;

        receiver.setAmount(receiver.getAmount().add(convertedAmount));
        sender.setAmount(sender.getAmount().subtract(amount));

        receiver = accountService.save(receiver);
        sender = accountService.save(sender);

        AccountBrokerMessage senderMessage = new AccountBrokerMessage(
                sender.getNumber(),
                sender.getCurrency(),
                Conversions.setScale(sender.getAmount())
        );
        AccountBrokerMessage receiverMessage = new AccountBrokerMessage(
                receiver.getNumber(),
                receiver.getCurrency(),
                Conversions.setScale(receiver.getAmount())
        );

        simpMessagingTemplate.convertAndSend(AccountBrokerMessage.DESTINATION, senderMessage);
        simpMessagingTemplate.convertAndSend(AccountBrokerMessage.DESTINATION, receiverMessage);

        return true;
    }
}
