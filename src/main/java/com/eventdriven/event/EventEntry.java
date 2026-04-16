package com.eventdriven.event;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * A single event recorded in the in-memory log.
 */
public class EventEntry {

    private final UUID eventId;
    private final LocalDateTime timestamp;
    private final String userId;
    private final String resourceId;
    private final EventType eventType;
    private final String details;

    public EventEntry(String userId, String resourceId, EventType eventType, String details) {
        this.eventId = UUID.randomUUID();
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
