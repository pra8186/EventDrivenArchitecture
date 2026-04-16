package com.eventdriven.controller;

import com.eventdriven.entity.EventRecord;
import com.eventdriven.event.EventType;
import com.eventdriven.repository.EventRecordRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST endpoint to access persisted events (consumed from Kafka, stored in DB).
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

    private final EventRecordRepository eventRecordRepository;

    public EventLogController(EventRecordRepository eventRecordRepository) {
        this.eventRecordRepository = eventRecordRepository;
    }

    /**
     * Returns all events, or filters by type if the query param is provided.
     */
    @GetMapping
    public List<EventRecord> getEvents(@RequestParam(required = false) String type) {
        if (type != null && !type.isBlank()) {
            EventType eventType = EventType.valueOf(type.toUpperCase());
            return eventRecordRepository.findByEventTypeOrderByTimestampDesc(eventType);
        }
        return eventRecordRepository.findAllByOrderByTimestampDesc();
    }

    /**
     * Returns events for a specific user.
     */
    @GetMapping("/user/{userId}")
    public List<EventRecord> getEventsByUser(@PathVariable String userId) {
        return eventRecordRepository.findByUserIdOrderByTimestampDesc(userId);
    }
}
