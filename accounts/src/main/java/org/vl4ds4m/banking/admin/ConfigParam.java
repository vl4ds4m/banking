package org.vl4ds4m.banking.admin;

import jakarta.persistence.*;

@Entity
@Table(name = "config_params")
public class ConfigParam {
    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "param_key")
    private Key key;

    @Column(name = "param_value",
        nullable = false)
    private String value;

    protected ConfigParam() {}

    public ConfigParam(Key key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ConfigParam other && this.key.equals(other.key);
    }

    public enum Key {
        FEE("fee", "0"),
        DUMMY("dummy", "nil");

        public final String title;

        public final String defaultValue;

        Key(String title, String defaultValue) {
            this.title = title;
            this.defaultValue = defaultValue;
        }
    }
}
