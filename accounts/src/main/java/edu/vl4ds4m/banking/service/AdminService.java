package edu.vl4ds4m.banking.service;

import edu.vl4ds4m.banking.dao.ConfigRepository;
import edu.vl4ds4m.banking.dto.Action;
import edu.vl4ds4m.banking.dto.Config;
import edu.vl4ds4m.banking.dto.UpdateConfigsRequest;
import io.micrometer.observation.annotation.Observed;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;

@Service
@Validated
public class AdminService {
    private static final String TOPIC_PROP = "${services.kafka.topic}";

    private static final Logger logger = LoggerFactory.getLogger(AdminService.class);

    private final ConfigRepository configRepository;
    private final KafkaTemplate<String, Action> kafkaTemplate;
    private final String topic;

    private BigDecimal fee;

    public AdminService(
            ConfigRepository configRepository,
            KafkaTemplate<String, Action> kafkaTemplate,
            @Value(TOPIC_PROP) String topic
    ) {
        this.configRepository = configRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
        loadFee();
    }

    private void loadFee() {
        fee = configRepository.findById(Config.Type.FEE)
                .map(config -> new BigDecimal(config.getValue()))
                .orElse(BigDecimal.ZERO);
        logger.info("Configuration[fee={}]", fee);
    }

    public BigDecimal getFee() {
        return fee;
    }

    @Observed
    @Transactional
    public void updateConfigs(@Valid UpdateConfigsRequest request) {
        configRepository.save(
                new Config(Config.Type.FEE, request.fee().toString()));
        logger.info("Persist Configuration[fee={}]", request.fee());

        Action action = new Action(Action.Type.UPDATE_FEE);
        kafkaTemplate.send(topic, action);
        logger.info("Send {}", action);
    }

    @KafkaListener(id = "configUpdater", topics = TOPIC_PROP)
    public void reloadConfigs(Action action) {
        logger.info("Reload configuration");
        if (Action.Type.UPDATE_FEE.equals(action.type())) {
            loadFee();
        }
    }
}
