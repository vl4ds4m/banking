package org.vl4ds4m.banking.accounts.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.vl4ds4m.banking.accounts.dao.SettingDao;
import org.vl4ds4m.banking.accounts.entity.setting.Setting;
import org.vl4ds4m.banking.common.entity.Money;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class SettingService {

    private final SettingDao settingDao;

    private Money cachedFeeFactor;

    private Money cachedMaxAmountPerOp;

    public Set<Setting> getAllSettings() {
        return settingDao.getAll();
    }

    public void updateSetting(Setting.Key key, String value) {
        Setting setting = Setting.create(key, value);
        settingDao.update(setting);
    }
}
