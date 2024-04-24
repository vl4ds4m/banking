package edu.tinkoff.dto;

import jakarta.persistence.*;

@Entity
public class Config {
    public static final String FEE = "fee";

    @Id
    private String name;

    @Basic(optional = false)
    private String value;

    public Config() {
    }

    public Config(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof String other && name.equals(other);
    }
}
