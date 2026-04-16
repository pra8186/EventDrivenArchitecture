package com.eventdriven.event;

import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * In-memory event log that records events when resources are created,
 * updated, or associated. Thread-safe via {@link CopyOnWriteArrayList}.
 */
@Service
public class EventLogService {

    private final List<EventEntry> log = new CopyOnWriteArrayList<>();

    /**
     * Records an event.
     *
     * @param userId     the user who triggered the action
     * @param resourceId the resource affected (user ID, profile ID, entry ID)
     * @param eventType  the type of event
     * @param details    human-readable description
     */
    public void record(String userId, String resourceId, EventType eventType, String details) {
        log.add(new EventEntry(userId, resourceId, eventType, details));
    }

    /**
     * Returns all recorded events, newest first.
     */
    public List<EventEntry> getAllEvents() {
        List<EventEntry> reversed = new java.util.ArrayList<>(log);
        Collections.reverse(reversed);
        return reversed;
    }

    /**
     * Returns events filtered by type.
     *
     * @param eventType the type to filter by
     */
    public List<EventEntry> getEventsByType(EventType eventType) {
        return log.stream()
                .filter(e -> e.getEventType() == eventType)
                .collect(Collectors.toList());
    }

    /**
     * Returns events for a specific user.
     *
     * @param userId the user's ID
     */
    public List<EventEntry> getEventsByUser(String userId) {
        return log.stream()
                .filter(e -> userId.equals(e.getUserId()))
                .collect(Collectors.toList());
    }

    /**
     * Returns the total number of recorded events.
     */
    public int count() {
        return log.size();
    }
}
