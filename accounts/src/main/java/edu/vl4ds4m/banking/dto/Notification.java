package edu.vl4ds4m.banking.dto;

import jakarta.persistence.Basic;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.time.Instant;

@Entity
public class Notification {
    @Id
    @GeneratedValue
    private Long id;

    @Basic(optional = false)
    Integer customerId;

    @Basic(optional = false)
    private String message;

    @Basic(optional = false)
    private Instant instant;

    public Notification() {
    }

    public Notification(int customerId, String message) {
        this.customerId = customerId;
        this.message = message;
        this.instant = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Instant getInstant() {
        return instant;
    }

    public void setInstant(Instant instant) {
        this.instant = instant;
    }
}
