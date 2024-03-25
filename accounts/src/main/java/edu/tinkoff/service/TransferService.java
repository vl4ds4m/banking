package edu.tinkoff.service;

import edu.tinkoff.dao.AccountRepository;
import edu.tinkoff.model.Account;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class TransferService {
    private final ConverterService converterService;
    private final AccountRepository accountRepository;

    public TransferService(ConverterService converterService, AccountRepository accountRepository) {
        this.converterService = converterService;
        this.accountRepository = accountRepository;
    }

    public void transfer(Account receiver, Account sender, double amount) {
        double convertedAmount;
        String receiverCurrency = receiver.getCurrency();
        String senderCurrency = sender.getCurrency();

        if (!senderCurrency.equals(receiverCurrency)) {
            Map<String, Object> responseBody = converterService.convert(
                    senderCurrency,
                    receiverCurrency,
                    amount
            );
            convertedAmount = (double) responseBody.get("amount");
        } else {
            convertedAmount = amount;
        }

        receiver.setAmount(receiver.getAmount() + convertedAmount);
        sender.setAmount(sender.getAmount() - amount);

        accountRepository.save(receiver);
        accountRepository.save(sender);
    }
}
