package com.eventdriven.core.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * Feign Client that discovers {@code event-service} via Eureka
 * and calls its REST endpoints over HTTP.
 */
@FeignClient(name = "event-service")
public interface EventServiceClient {

    /** Record a new event. */
    @PostMapping("/api/events")
    void recordEvent(@RequestBody EventRequest request);

    /** Get all events, optionally filtered by type. */
    @GetMapping("/api/events")
    List<Map<String, Object>> getEvents(@RequestParam(required = false) String type);

    /** Get events for a specific user. */
    @GetMapping("/api/events/user/{userId}")
    List<Map<String, Object>> getEventsByUser(@PathVariable String userId);
}
