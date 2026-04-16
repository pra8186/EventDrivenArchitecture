package com.eventdriven.controller;

import com.eventdriven.event.EventEntry;
import com.eventdriven.event.EventLogService;
import com.eventdriven.event.EventType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST endpoint to access the in-memory event log.
 *
 * <ul>
 *   <li>{@code GET /api/events}              — all events (newest first)</li>
 *   <li>{@code GET /api/events?type=...}     — filter by event type</li>
 *   <li>{@code GET /api/events/user/{userId}} — filter by user</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/events")
public class EventLogController {

    private final EventLogService eventLogService;

    public EventLogController(EventLogService eventLogService) {
        this.eventLogService = eventLogService;
    }

    /**
     * Returns all events, or filters by type if the query param is provided.
     */
    @GetMapping
    public List<EventEntry> getEvents(@RequestParam(required = false) String type) {
        if (type != null && !type.isBlank()) {
            EventType eventType = EventType.valueOf(type.toUpperCase());
            return eventLogService.getEventsByType(eventType);
        }
        return eventLogService.getAllEvents();
    }

    /**
     * Returns events for a specific user.
     */
    @GetMapping("/user/{userId}")
    public List<EventEntry> getEventsByUser(@PathVariable String userId) {
        return eventLogService.getEventsByUser(userId);
    }
}
