package com.eventdriven.repository;

import com.eventdriven.entity.EventRecord;
import com.eventdriven.event.EventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

public interface EventRecordRepository extends JpaRepository<EventRecord, UUID> {

    List<EventRecord> findByEventTypeOrderByTimestampDesc(EventType eventType);

    List<EventRecord> findByUserIdOrderByTimestampDesc(String userId);

    List<EventRecord> findAllByOrderByTimestampDesc();
}
