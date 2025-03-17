package edu.vl4ds4m.banking.messaging;

import edu.vl4ds4m.banking.account.Account;
import io.micrometer.observation.annotation.Observed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class SimpMessagingService {
    private static final Logger logger = LoggerFactory.getLogger(SimpMessagingService.class);

    private final SimpMessagingTemplate template;

    public SimpMessagingService(SimpMessagingTemplate template) {
        this.template = template;
    }

    @Observed
    public void sendMessage(Account account) {
        AccountBrokerMessage message = new AccountBrokerMessage(
                account.getNumber(),
                account.getCurrency(),
                account.getAmount());
        logger.debug("Send {}", message);
        template.convertAndSend(AccountBrokerMessage.DESTINATION, message);
    }
}
