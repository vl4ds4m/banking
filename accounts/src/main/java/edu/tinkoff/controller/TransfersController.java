package edu.tinkoff.controller;

import edu.tinkoff.dao.AccountRepository;
import edu.tinkoff.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("transfers")
public class TransfersController {

    @Autowired
    private ConverterInvoker converterInvoker;

    @Autowired
    private AccountRepository accountRepository;

    @PostMapping
    public ResponseEntity<Object> transfer(@RequestBody Map<String, Number> requestBody) {
        try {
            int receiverAccount;
            int senderAccount;
            double exactAmount;
            try {
                receiverAccount = (int) requestBody.get("receiverAccount");
                senderAccount = (int) requestBody.get("senderAccount");
                exactAmount = (double) requestBody.get("amountInSenderCurrency");
            } catch (NullPointerException e) {
                return ResponseEntity.status(400).build();
            }

            double amount = BigDecimal.valueOf(exactAmount)
                    .setScale(2, RoundingMode.HALF_EVEN)
                    .doubleValue();

            if (amount <= 0.0) {
                return ResponseEntity.status(400).build();
            }

            Optional<Account> optionalReceiver = accountRepository.findById(receiverAccount);
            Optional<Account> optionalSender = accountRepository.findById(senderAccount);

            if (optionalReceiver.isEmpty() || optionalSender.isEmpty()) {
                return ResponseEntity.status(400).build();
            }

            Account receiver = optionalReceiver.get();
            Account sender = optionalSender.get();

            if (sender.getAmount() < amount) {
                return ResponseEntity.status(400).build();
            }

            double convertedAmount;
            String receiverCurrency = receiver.getCurrency();
            String senderCurrency = sender.getCurrency();

            if (!senderCurrency.equals(receiverCurrency)) {
                Map<String, Object> responseBody = converterInvoker.convert(
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

            return ResponseEntity.status(200).build();

        } catch (RuntimeException e) {
            return ResponseEntity.status(500).build();
        }
    }
}
