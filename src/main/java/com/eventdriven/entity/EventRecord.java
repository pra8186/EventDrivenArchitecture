package com.eventdriven.entity;

import com.eventdriven.event.EventType;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * JPA entity for events persisted to the {@code event_log} table.
 * Consumed from Kafka and saved by {@code EventConsumerService}.
 */
@Entity
@Table(name = "event_log", indexes = {
        @Index(name = "idx_event_log_user", columnList = "user_id"),
        @Index(name = "idx_event_log_type", columnList = "event_type")
})
public class EventRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "event_id", nullable = false, updatable = false)
    private UUID eventId;

    @Column(name = "timestamp", nullable = false, updatable = false)
    private LocalDateTime timestamp;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "resource_id", nullable = false)
    private String resourceId;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private EventType eventType;

    @Column(name = "details")
    private String details;

    protected EventRecord() {}

    public EventRecord(String userId, String resourceId, EventType eventType, String details) {
        this.timestamp = LocalDateTime.now();
        this.userId = userId;
        this.resourceId = resourceId;
        this.eventType = eventType;
        this.details = details;
    }

    public UUID getEventId() { return eventId; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getUserId() { return userId; }
    public String getResourceId() { return resourceId; }
    public EventType getEventType() { return eventType; }
    public String getDetails() { return details; }
}
