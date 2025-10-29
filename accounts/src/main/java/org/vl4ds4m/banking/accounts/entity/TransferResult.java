package org.vl4ds4m.banking.accounts.entity;

import org.vl4ds4m.banking.common.entity.Money;

public record TransferResult(

        Money totalSenderMoney,

        Money totalReceiverMoney
) {}
