package org.vl4ds4m.banking.accounts.deprecation.notification;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

// @Service
@EnableConfigurationProperties(NotificationProperties.class)
public class NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository repository;
    private final RestTemplate restTemplate;
    private final String url;
    private final int notificationCount;

    public NotificationService(
        NotificationRepository repository,
        NotificationProperties properties,
        RestTemplate restTemplate
    ) {
        this.repository = repository;
        this.restTemplate = restTemplate;
        this.url = properties.url();
        this.notificationCount = properties.count();
    }

    public void save(int customerId, int accountId, BigDecimal amount, BigDecimal balance) {
        String message = String.format("Счет %d. Операция: %s. Баланс: %s",
            accountId, amount, balance);
        Notification notification = repository.save(
            new Notification(customerId, message, Instant.now()));
        logger.debug("Notification[id={}] saved", notification.getId());
    }

    @Transactional
    public void post() {
        List<Notification> posted = repository
            .findNextK(notificationCount)
            .stream()
            .filter(this::post)
            .toList();
        logger.debug("{} notification(s)'re posted", posted.size());
        repository.deleteAll(posted);
    }

    private boolean post(Notification notification) {
        NotificationRequest request = new NotificationRequest(
            notification.getCustomerId(),
            notification.getMessage());
        ResponseEntity<?> responseEntity;
        try {
            responseEntity = restTemplate.postForEntity(url, request, Void.class);
        } catch (RuntimeException e) {
            return false;
        }
        return !responseEntity.getStatusCode().isError();
    }
}
