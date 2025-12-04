package org.vl4ds4m.banking.accounts.service.transaction;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.vl4ds4m.banking.common.entity.Transaction;
import org.vl4ds4m.banking.common.entity.kafka.TransactionMessage;
import org.vl4ds4m.banking.common.util.To;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final KafkaTemplate<String, TransactionMessage> kafkaTemplate;

    @Override
    public void sendTransactions(Transaction... transactions) {
        if (transactions.length == 0) return;

        var id = UUID.randomUUID();
        var timestamp = Instant.now();

        for (var t : transactions) {
            var message = TransactionMessage.create(t, id, timestamp);
            kafkaTemplate.send(TransactionMessage.KAFKA_TOPIC, message);
            log.debug("Send {} to kafka topic [{}]",
                    To.string(TransactionMessage.class, id, t.accountNumber()),
                    TransactionMessage.KAFKA_TOPIC);
        }
    }

}
