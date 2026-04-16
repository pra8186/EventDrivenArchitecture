package com.eventdriven.eventservice.controller;

import com.eventdriven.eventservice.dto.EventRequest;
import com.eventdriven.eventservice.entity.EventRecord;
import com.eventdriven.eventservice.event.EventProducerService;
import com.eventdriven.eventservice.event.EventType;
import com.eventdriven.eventservice.repository.EventRecordRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST endpoints for the event log.
 *
 * <ul>
 *   <li>{@code POST /api/events}              — receive event from core-service, publish to Kafka</li>
 *   <li>{@code GET  /api/events}              — all events (newest first)</li>
 *   <li>{@code GET  /api/events?type=...}     — filter by type</li>
 *   <li>{@code GET  /api/events/user/{userId}} — filter by user</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/events")
public class EventLogController {

    private final EventProducerService eventProducerService;
    private final EventRecordRepository eventRecordRepository;

    public EventLogController(EventProducerService eventProducerService,
                              EventRecordRepository eventRecordRepository) {
        this.eventProducerService = eventProducerService;
        this.eventRecordRepository = eventRecordRepository;
    }

    /** Receive event from core-service (Feign), publish to Kafka. */
    @PostMapping
    public ResponseEntity<Void> recordEvent(@RequestBody EventRequest request) {
        EventType eventType = EventType.valueOf(request.getEventType());
        eventProducerService.publish(
                request.getUserId(), request.getResourceId(),
                eventType, request.getDetails()
        );
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    /** All events, optionally filtered by type. */
    @GetMapping
    public List<EventRecord> getEvents(@RequestParam(required = false) String type) {
        if (type != null && !type.isBlank()) {
            EventType eventType = EventType.valueOf(type.toUpperCase());
            return eventRecordRepository.findByEventTypeOrderByTimestampDesc(eventType);
        }
        return eventRecordRepository.findAllByOrderByTimestampDesc();
    }

    /** Events for a specific user. */
    @GetMapping("/user/{userId}")
    public List<EventRecord> getEventsByUser(@PathVariable String userId) {
        return eventRecordRepository.findByUserIdOrderByTimestampDesc(userId);
    }
}
