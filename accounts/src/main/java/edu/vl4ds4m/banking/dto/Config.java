package edu.vl4ds4m.banking.dto;

import jakarta.persistence.*;

@Entity
public class Config {
    public enum Type {
        FEE
    }

    @Id
    @Enumerated(EnumType.STRING)
    private Type name;

    @Basic(optional = false)
    private String value;

    public Config() {
    }

    public Config(Type name, String value) {
        this.name = name;
        this.value = value;
    }

    public Type getName() {
        return name;
    }

    public void setName(Type name) {
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
        return obj instanceof Config other && name.equals(other.name);
    }
}
