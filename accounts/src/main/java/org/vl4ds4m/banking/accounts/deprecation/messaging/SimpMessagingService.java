package org.vl4ds4m.banking.accounts.deprecation.messaging;

import io.micrometer.observation.annotation.Observed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.vl4ds4m.banking.accounts.entity.Account;

// @Service
public class SimpMessagingService {
    private static final Logger logger = LoggerFactory.getLogger(SimpMessagingService.class);

    private final String destinationPrefix;
    private final SimpMessagingTemplate template;

    public SimpMessagingService(MessagingProperties properties, SimpMessagingTemplate template) {
        this.destinationPrefix = properties.destinationPrefix();
        this.template = template;
    }

    @Observed
    public void sendMessage(Account account) {
        AccountBrokerMessage message = new AccountBrokerMessage(
                account.number(),
                account.currency(),
                account.money().amount());
        String destination = destinationPrefix + AccountBrokerMessage.DESTINATION;
        logger.debug("Send {} to {}", message, destination);
        template.convertAndSend(destination, message);
    }
}
