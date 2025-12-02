package org.vl4ds4m.banking.accounts.entity.setting;

public sealed interface Setting permits
        TransferFeeSetting,
        MaxMoneyPerOpSetting
{
    Key key();

    String value();

    static Setting create(Key key, String value) {
        return switch (key) {
            case TRANSFER_FEE -> new TransferFeeSetting(value);
            case MAX_MONEY_PER_OPERATION -> new MaxMoneyPerOpSetting(value);
        };
    }

    enum Key {
        TRANSFER_FEE,
        MAX_MONEY_PER_OPERATION
    }
}
