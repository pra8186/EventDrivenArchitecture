package com.eventdriven.event;

import com.eventdriven.entity.EventRecord;
import com.eventdriven.repository.EventRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Kafka consumer that reads {@link EventEntry} messages from the
 * {@code tax-events} topic and persists them to the database as
 * {@link EventRecord} entities.
 */
@Service
public class EventConsumerService {

    private static final Logger log = LoggerFactory.getLogger(EventConsumerService.class);

    private final EventRecordRepository eventRecordRepository;

    public EventConsumerService(EventRecordRepository eventRecordRepository) {
        this.eventRecordRepository = eventRecordRepository;
    }

    /**
     * Listens to the Kafka topic and saves each event to the database.
     *
     * @param entry the deserialized event from Kafka
     */
    @KafkaListener(topics = "${app.kafka.topic:tax-events}", groupId = "eventdriven-group")
    public void consume(EventEntry entry) {
        log.info("Consumed event: {} for user {} resource {}",
                entry.getEventType(), entry.getUserId(), entry.getResourceId());

        EventRecord record = new EventRecord(
                entry.getUserId(),
                entry.getResourceId(),
                entry.getEventType(),
                entry.getDetails()
        );
        eventRecordRepository.save(record);
    }
}
