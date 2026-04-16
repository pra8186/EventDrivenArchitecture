package com.eventdriven.eventservice.event;

import com.eventdriven.eventservice.entity.EventRecord;
import com.eventdriven.eventservice.repository.EventRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Kafka consumer — reads events from the topic and persists to the database.
 */
@Service
public class EventConsumerService {

    private static final Logger log = LoggerFactory.getLogger(EventConsumerService.class);
    private final EventRecordRepository eventRecordRepository;

    public EventConsumerService(EventRecordRepository eventRecordRepository) {
        this.eventRecordRepository = eventRecordRepository;
    }

    @KafkaListener(topics = "${app.kafka.topic:tax-events}", groupId = "eventdriven-group")
    public void consume(EventEntry entry) {
        log.info("Consumed event: {} for user {}", entry.getEventType(), entry.getUserId());
        EventRecord record = new EventRecord(
                entry.getUserId(), entry.getResourceId(),
                entry.getEventType(), entry.getDetails()
        );
        eventRecordRepository.save(record);
    }
}
