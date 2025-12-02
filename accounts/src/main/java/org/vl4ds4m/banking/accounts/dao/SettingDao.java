package org.vl4ds4m.banking.accounts.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.vl4ds4m.banking.accounts.entity.setting.Setting;
import org.vl4ds4m.banking.accounts.repository.SettingRepository;
import org.vl4ds4m.banking.accounts.repository.entity.SettingRe;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Repository
@RequiredArgsConstructor
public class SettingDao {

    private final SettingRepository settingRepository;

    public Set<Setting> getAll() {
        var settings = settingRepository.findAll();
        return StreamSupport.stream(settings.spliterator(), false)
                .map(re -> Setting.create(re.getKey(), re.getValue()))
                .collect(Collectors.toSet());
    }

    public Setting getByKey(Setting.Key key) {
        return settingRepository.findById(key)
                .map(re -> Setting.create(key, re.getValue()))
                .orElseThrow();
    }

    public void update(Setting setting) {
        var re = new SettingRe();
        re.setKey(setting.key());
        re.setValue(setting.value());
        settingRepository.save(re);
    }
}
