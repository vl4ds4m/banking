package org.vl4ds4m.banking.accounts.repository.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.vl4ds4m.banking.accounts.entity.setting.Setting;

import static org.vl4ds4m.banking.accounts.repository.entity.SettingRe.ColumnNames.*;
import static org.vl4ds4m.banking.accounts.repository.entity.SettingRe.TABLE_NAME;

@Entity
@Table(name = TABLE_NAME)
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SettingRe {

    static final String TABLE_NAME = "settings";

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = ID)
    @EqualsAndHashCode.Include
    private Setting.Key key;

    @Column(name = VALUE,
            nullable = false)
    private String value;

    static class ColumnNames {

        static final String ID = "id";

        static final String VALUE = "value";
    }
}
