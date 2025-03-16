package edu.vl4ds4m.banking.notification;

import org.springframework.scheduling.annotation.Scheduled;

//@Component
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
