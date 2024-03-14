package edu.tinkoff.service;

import edu.tinkoff.dao.AccountRepository;
import edu.tinkoff.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class TransferService {

    @Autowired
    private ConverterService converterService;

    @Autowired
    private AccountRepository accountRepository;

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
