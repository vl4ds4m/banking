package org.vl4ds4m.banking.accounts.repository;

import org.springframework.data.repository.CrudRepository;
import org.vl4ds4m.banking.accounts.entity.setting.Setting;
import org.vl4ds4m.banking.accounts.repository.entity.SettingRe;

public interface SettingRepository extends CrudRepository<SettingRe, Setting.Key> {}
