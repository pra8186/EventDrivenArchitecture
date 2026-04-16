package com.eventdriven.eventservice.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Publishes events to the Kafka topic.
 */
@Service
public class EventProducerService {

    private static final Logger log = LoggerFactory.getLogger(EventProducerService.class);

    private final KafkaTemplate<String, EventEntry> kafkaTemplate;
    private final String topicName;

    public EventProducerService(KafkaTemplate<String, EventEntry> kafkaTemplate,
                                @Value("${app.kafka.topic:tax-events}") String topicName) {
        this.kafkaTemplate = kafkaTemplate;
        this.topicName = topicName;
    }

    public void publish(String userId, String resourceId, EventType eventType, String details) {
        EventEntry entry = new EventEntry(userId, resourceId, eventType, details);
        kafkaTemplate.send(topicName, userId, entry);
        log.info("Published event: {} for user {}", eventType, userId);
    }
}
