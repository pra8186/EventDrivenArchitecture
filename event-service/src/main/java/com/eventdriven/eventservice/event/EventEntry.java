package com.eventdriven.eventservice.event;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Kafka message payload. Jackson-serializable.
 */
public class EventEntry {

    private UUID eventId;
    private LocalDateTime timestamp;
    private String userId;
    private String resourceId;
    private EventType eventType;
    private String details;

    public EventEntry() {}

    public EventEntry(String userId, String resourceId, EventType eventType, String details) {
        this.eventId = UUID.randomUUID();
        this.timestamp = LocalDateTime.now();
        this.userId = userId;
        this.resourceId = resourceId;
        this.eventType = eventType;
        this.details = details;
    }

    public UUID getEventId() { return eventId; }
    public void setEventId(UUID eventId) { this.eventId = eventId; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getResourceId() { return resourceId; }
    public void setResourceId(String resourceId) { this.resourceId = resourceId; }
    public EventType getEventType() { return eventType; }
    public void setEventType(EventType eventType) { this.eventType = eventType; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
}
