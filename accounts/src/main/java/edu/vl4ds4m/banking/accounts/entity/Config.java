package edu.vl4ds4m.banking.accounts.entity;

import edu.vl4ds4m.banking.Conversions;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "configs")
public class Config {
    public enum Type {
        FEE
    }

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "name")
    private Type name;

    @Column(name = "value",
        nullable = false,
        precision = Conversions.PRECISION,
        scale = Conversions.SCALE)
    private BigDecimal value;

    protected Config() {}

    public Config(Type name, BigDecimal value) {
        this.name = name;
        this.value = value;
    }

    public BigDecimal getValue() {
        return value;
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
