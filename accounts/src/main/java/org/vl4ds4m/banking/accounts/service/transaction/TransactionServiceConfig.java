package org.vl4ds4m.banking.accounts.service.transaction;

import org.jspecify.annotations.Nullable;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.KafkaTemplate;
import org.vl4ds4m.banking.common.Common;
import org.vl4ds4m.banking.common.entity.kafka.TransactionMessage;

@Configuration
public class TransactionServiceConfig implements EnvironmentAware {

    @Nullable
    private Environment environment;

    @Bean
    public TransactionService transactionService(KafkaTemplate<String, TransactionMessage> kafkaTemplate) {
        return useKafka()
                ? new TransactionServiceImpl(kafkaTemplate)
                : new DummyTransactionService();
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    private Environment getEnvironment() {
        if (environment == null) {
            throw new IllegalStateException();
        }
        return environment;
    }

    private boolean useKafka() {
        Environment env = getEnvironment();
        return env.matchesProfiles("kafka") || !Common.isRunStandalone(env);
    }

}
