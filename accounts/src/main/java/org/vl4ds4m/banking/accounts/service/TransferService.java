package org.vl4ds4m.banking.accounts.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.vl4ds4m.banking.accounts.dao.AccountDao;
import org.vl4ds4m.banking.accounts.entity.Account;
import org.vl4ds4m.banking.accounts.entity.TransferResult;
import org.vl4ds4m.banking.accounts.service.expection.EntityNotFoundException;
import org.vl4ds4m.banking.accounts.service.expection.ServiceException;
import org.vl4ds4m.banking.common.entity.Money;
import org.vl4ds4m.banking.common.util.To;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransferService {

    private final AccountDao accountDao;

    private final ConverterService converterService;

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public TransferResult transferMoney(long senderNumber, long receiverNumber, Money money) {
        checkAccountExists(senderNumber);
        var sender = accountDao.getByNumber(senderNumber);

        if (senderNumber == receiverNumber) {
            log.warn("Transfer money to the same account is redundant, nothing to change.");
            return new TransferResult(sender.money(), sender.money());
        }

        checkAccountExists(receiverNumber);
        var receiver = accountDao.getByNumber(receiverNumber);

        if (money.isEmpty()) {
            log.warn("Zero money transfer is redundant, nothing to change.");
            return new TransferResult(sender.money(), receiver.money());
        }

        if (sender.money().compareTo(money) < 0) {
            throw new ServiceException("Sender " + To.string(Account.class, senderNumber) +
                    " doesn't have enough money for transfer operation.");
        }

        var converted = converterService.convert(sender.currency(), receiver.currency(), money);

        var totalSenderMoney = sender.money().subtract(money);
        var totalReceiverMoney = receiver.money().add(converted);

        accountDao.updateMoney(senderNumber, totalSenderMoney);
        accountDao.updateMoney(receiverNumber, totalReceiverMoney);
        log.info("Transfer operation {} -> {} is done",
                To.string(Account.class, senderNumber),
                To.string(Account.class, receiverNumber));

        return new TransferResult(totalSenderMoney, totalReceiverMoney);
    }

    private void checkAccountExists(long number) {
        if (!accountDao.existsByNumber(number)) {
            throw new EntityNotFoundException(Account.class, number);
        }
    }
}
