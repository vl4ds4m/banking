package edu.vl4ds4m.banking.notification;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "customer_id", nullable = false)
    private Integer customerId;

    @Column(name = "message", nullable = false)
    private String message;

    @Column(name = "time", nullable = false)
    private Instant time;

    protected Notification() {}

    public Notification(int customerId, String message, Instant time) {
        this.customerId = customerId;
        this.message = message;
        this.time = time;
    }

    public Long getId() {
        return id;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public String getMessage() {
        return message;
    }

    public Instant getTime() {
        return time;
    }
}
