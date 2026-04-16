package com.eventdriven.eventservice.repository;

import com.eventdriven.eventservice.entity.EventRecord;
import com.eventdriven.eventservice.event.EventType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface EventRecordRepository extends JpaRepository<EventRecord, UUID> {

    List<EventRecord> findByEventTypeOrderByTimestampDesc(EventType eventType);
    List<EventRecord> findByUserIdOrderByTimestampDesc(String userId);
    List<EventRecord> findAllByOrderByTimestampDesc();
}
