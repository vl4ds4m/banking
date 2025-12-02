package org.vl4ds4m.banking.accounts.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.vl4ds4m.banking.accounts.openapi.server.api.SettingsApi;
import org.vl4ds4m.banking.accounts.openapi.server.model.Setting;
import org.vl4ds4m.banking.accounts.service.SettingService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SettingController implements SettingsApi {

    private final SettingService settingService;

    @Override
    public ResponseEntity<List<Setting>> getSettings() {
        var settings = settingService.getAllSettings()
                .stream()
                .map(s -> new Setting(s.key().name(), s.value()))
                .toList();
        return ResponseEntity.ok(settings);
    }

    @Override
    public ResponseEntity<Void> updateSetting(Setting setting) {
        settingService.updateSetting(
                org.vl4ds4m.banking.accounts.entity.setting.Setting.Key.valueOf(setting.getKey()),
                setting.getValue());
        return ResponseEntity.ok().build();
    }
}
