package edu.vl4ds4m.banking.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(
    prefix = NotificationProperties.PREFIX,
    name = "enabled",
    matchIfMissing = true)
public class NotificationTask {
    private static final Logger logger = LoggerFactory.getLogger(NotificationTask.class);

    private final NotificationService service;

    public NotificationTask(NotificationService service) {
        this.service = service;
        logger.info("Create NotificationTask to schedule notification sending");
    }

    private static final String DELAY = "${" + NotificationProperties.PREFIX + ".delay}";

    @Scheduled(
        initialDelayString = DELAY,
        fixedDelayString = DELAY)
    public void post() {
        logger.debug("Run task to send notifications");
        service.post();
    }
}
