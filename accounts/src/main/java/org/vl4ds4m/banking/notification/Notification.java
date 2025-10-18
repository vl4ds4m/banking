package org.vl4ds4m.banking.notification;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = Notification.TABLE_NAME)
public class Notification {
    static final String TABLE_NAME = "notifications";
    static final String ID = "id";
    static final String CUSTOMER_ID = "customer_id";
    static final String MESSAGE = "message";
    static final String TIME = "time";

    @Id
    @GeneratedValue
    @Column(name = ID)
    private Long id;

    @Column(name = CUSTOMER_ID, nullable = false)
    private Integer customerId;

    @Column(name = MESSAGE, nullable = false)
    private String message;

    @Column(name = TIME, nullable = false)
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
