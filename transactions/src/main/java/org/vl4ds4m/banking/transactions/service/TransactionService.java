package org.vl4ds4m.banking.transactions.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.PartitionInfo;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.TopicPartitionOffset;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vl4ds4m.banking.common.entity.kafka.TransactionMessage;
import org.vl4ds4m.banking.common.exception.InvalidQueryException;
import org.vl4ds4m.banking.common.util.To;
import org.vl4ds4m.banking.transactions.repository.TransactionRepository;
import org.vl4ds4m.banking.transactions.repository.entity.TransactionRe;

import java.util.ArrayList;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    private final KafkaTemplate<String, TransactionMessage> kafkaTemplate;

    private final ConsumerFactory<String, TransactionMessage> consumerFactory;

    @KafkaListener(topics = TransactionMessage.KAFKA_TOPIC)
    @Transactional
    public void saveTransaction(TransactionMessage message) {
        var entity = new TransactionRe(
                message.transactionId(),
                message.accountNumber(),
                message.amount(),
                message.withdraw(),
                message.timestamp());
        transactionRepository.insert(entity);
        log.info("{} saved", To.string(TransactionMessage.class, message.transactionId()));
    }

    public void rereadAndSave(int partition, long offset, int count) {
        if (partition < 0) {
            throw new InvalidQueryException("Partition must be non-negative.");
        }
        if (offset < 0L) {
            throw new InvalidQueryException("Offset must be non-negative.");
        }
        if (count <= 0) {
            throw new InvalidQueryException("Count must be positive.");
        }

        boolean hasPartition = kafkaTemplate.partitionsFor(TransactionMessage.KAFKA_TOPIC)
                .stream()
                .mapToInt(PartitionInfo::partition)
                .anyMatch(p -> p == partition);
        if (!hasPartition) {
            throw new InvalidQueryException("Partition " + partition + " not found.");
        }

        var requested = new ArrayList<TopicPartitionOffset>(count);
        for (int i = 0; i < count; i++) {
            requested.add(new TopicPartitionOffset(TransactionMessage.KAFKA_TOPIC, partition, offset + i));
        }

        kafkaTemplate.setConsumerFactory(consumerFactory);
        var records = kafkaTemplate.receive(requested);

        for (var r : records) {
            saveTransaction(r.value());
        }
    }

}
