package edu.tinkoff.service;

import edu.tinkoff.dao.ConfigRepository;
import edu.tinkoff.dto.Config;
import edu.tinkoff.dto.UpdateConfigsRequest;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;

@Service
@Validated
public class AdminService {
    private final ConfigRepository configRepository;

    private BigDecimal fee;

    public AdminService(ConfigRepository configRepository) {
        this.configRepository = configRepository;
        loadConfigs();
    }

    private void loadConfigs() {
        fee = configRepository.findById(Config.FEE)
                .map(config -> new BigDecimal(config.getValue()))
                .orElse(BigDecimal.ZERO);
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void updateConfigs(@Valid UpdateConfigsRequest request) {
        fee = request.fee();
        Config feeConfig = new Config(Config.FEE, fee.toString());
        configRepository.save(feeConfig);
    }
}
