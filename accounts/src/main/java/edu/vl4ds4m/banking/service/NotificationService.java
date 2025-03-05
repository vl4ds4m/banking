package edu.vl4ds4m.banking.service;

import edu.vl4ds4m.banking.dao.NotificationRepository;
import edu.vl4ds4m.banking.dto.Notification;
import edu.vl4ds4m.banking.dto.NotificationRequest;
import edu.vl4ds4m.banking.properties.NotificationProperties;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;

@Service
public class NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository notificationRepository;
    private final RestTemplate restTemplate;
    private final String postUrl;
    private final int notificationCount;

    public NotificationService(
            NotificationRepository notificationRepository,
            NotificationProperties properties,
            RestTemplate restTemplate
    ) {
        this.notificationRepository = notificationRepository;
        this.restTemplate = restTemplate;
        this.postUrl = properties.url() + "/notification";
        this.notificationCount = properties.count();
    }

    public void save(int customerId, int accountId, BigDecimal amount, BigDecimal balance) {
        String message = String.format("Счет %d. Операция: %s. Баланс: %s",
                accountId, amount, balance);
        Notification notification = notificationRepository.save(new Notification(customerId, message));
        logger.debug("Persist Notification[id={}]", notification.getId());
    }

    @Transactional
    public void checkAndPost() {
        List<Notification> notifications =
                notificationRepository.findTopKOrderByInstantAsc(notificationCount);
        List<Notification> receivedNotifications = notifications.stream()
                .filter(this::post).toList();
        notificationRepository.deleteAll(receivedNotifications);
    }

    private boolean post(Notification notification) {
        NotificationRequest request = new NotificationRequest(
                notification.getCustomerId(),
                notification.getMessage());
        ResponseEntity<?> responseEntity;
        try {
            responseEntity = restTemplate.postForEntity(postUrl, request, Void.class);
            logger.debug("Post Notification[id={}]", notification.getId());
        } catch (RuntimeException e) {
            return false;
        }
        return !responseEntity.getStatusCode().isError();
    }
}
