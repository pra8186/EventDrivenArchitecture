package com.eventdriven.core.repository;

import com.eventdriven.core.entity.WorkDayEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface WorkDayEntryRepository extends JpaRepository<WorkDayEntry, UUID> {

    List<WorkDayEntry> findByTaxProfile_ProfileId(UUID profileId);
}
