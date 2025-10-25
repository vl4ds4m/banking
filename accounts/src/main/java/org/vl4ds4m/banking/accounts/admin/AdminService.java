package org.vl4ds4m.banking.accounts.admin;

import io.micrometer.observation.annotation.Observed;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.vl4ds4m.banking.Conversions;
import org.vl4ds4m.banking.accounts.admin.dto.Action;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class AdminService {
    private static final String PROP_TOPIC_UPD_CFG = "${services.kafka.topic.update-config}";

    private static final Logger logger = LoggerFactory.getLogger(AdminService.class);

    private final ConfigRepository configRepository;
    private final KafkaTemplate<String, Action> kafkaTemplate;
    private final String updCfgTopic;

    private BigDecimal fee;

    public AdminService(
        ConfigRepository configRepository,
        //KafkaTemplate<String, Action> kafkaTemplate,
        @Value(PROP_TOPIC_UPD_CFG) String topic
    ) {
        this.configRepository = configRepository;
        this.kafkaTemplate = null; // kafkaTemplate;
        this.updCfgTopic = topic;
        loadFee();
        loadParam(ConfigParam.Key.DUMMY);
    }

    private void loadFee() {
        String value = loadParam(ConfigParam.Key.FEE);
        this.fee = Conversions.setScale(new BigDecimal(value));
    }

    private String loadParam(ConfigParam.Key key) {
        String value = configRepository.findById(key)
            .map(ConfigParam::getValue)
            .orElseGet(() -> {
                logger.debug(
                    "Config param '{}' isn't set. Return default value = {}",
                    key.title, key.defaultValue);
                return key.defaultValue;
            });
        logger.info("Load configuration[{}={}]", key.title, value);
        return value;
    }

    public BigDecimal getFee() {
        return fee;
    }

    @Observed
    @Transactional
    public void updateConfig(Map<String, String> config) {
        updateFee(config);
    }

    private void updateFee(Map<String, String> config) {
        String value = config.get(ConfigParam.Key.FEE.title);
        if (value == null) return;

        BigDecimal fee;
        try {
            fee = Conversions.setScale(value);
            if (BigDecimal.ONE.compareTo(fee) < 0 || BigDecimal.ZERO.compareTo(fee) > 0) {
                throw new Exception("Fee isn't in range [0; 1]");
            }
        } catch (Exception e) {
            logger.warn("Fee is invalid", e);
            return;
        }

        updateConfigParam(ConfigParam.Key.FEE, fee.toString());
        this.fee = fee; // sendAction(Action.Type.UPDATE_FEE);
    }

    private void updateConfigParam(ConfigParam.Key key, String value) {
        logger.info("Update configuration[{}={}]", key.title, value);
        configRepository.save(new ConfigParam(key, value));
    }

    private void sendAction(Action.Type type) {
        Action action = new Action(type);
        logger.debug("Send {} to queue", action);
        kafkaTemplate.send(updCfgTopic, action);
    }

    // @KafkaListener(id = "config_updater", topics = PROP_TOPIC_UPD_CFG)
    public void reloadConfig(Action action) {
        logger.debug("Reload configuration");
        if (action.type().equals(Action.Type.UPDATE_FEE)) {
            loadFee();
        }
    }
}
