package edu.vl4ds4m.banking.task;

import edu.vl4ds4m.banking.service.NotificationService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class NotificationTask {
    private final NotificationService service;

    public NotificationTask(NotificationService service) {
        this.service = service;
    }

    @Scheduled(
            initialDelayString = "${services.notification.delay}",
            fixedDelayString = "${services.notification.delay}")
    public void checkAndPost() {
        service.checkAndPost();
    }
}
