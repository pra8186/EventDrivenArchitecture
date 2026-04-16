package com.eventdriven.core.client;

/**
 * DTO sent to the event-service via Feign to record an event.
 */
public class EventRequest {

    private String userId;
    private String resourceId;
    private String eventType;
    private String details;

    public EventRequest() {}

    public EventRequest(String userId, String resourceId, String eventType, String details) {
        this.userId = userId;
        this.resourceId = resourceId;
        this.eventType = eventType;
        this.details = details;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getResourceId() { return resourceId; }
    public void setResourceId(String resourceId) { this.resourceId = resourceId; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
}
