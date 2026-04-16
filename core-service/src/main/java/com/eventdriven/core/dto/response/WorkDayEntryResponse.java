package com.eventdriven.core.dto.response;

import com.eventdriven.core.entity.WorkDayEntry;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for work day entry data. Exposes IDs, not full nested entities.
 */
public class WorkDayEntryResponse {

    private final UUID entryId;
    private final UUID profileId;
    private final String stateCode;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final BigDecimal income;
    private final String workType;
    private final LocalDateTime createdAt;

    private WorkDayEntryResponse(UUID entryId, UUID profileId, String stateCode,
                                 LocalDate startDate, LocalDate endDate, BigDecimal income,
                                 String workType, LocalDateTime createdAt) {
        this.entryId = entryId;
        this.profileId = profileId;
        this.stateCode = stateCode;
        this.startDate = startDate;
        this.endDate = endDate;
        this.income = income;
        this.workType = workType;
        this.createdAt = createdAt;
    }

    public static WorkDayEntryResponse from(WorkDayEntry entry) {
        return new WorkDayEntryResponse(
                entry.getEntryId(),
                entry.getTaxProfile().getProfileId(),
                entry.getState().getStateCode(),
                entry.getStartDate(), entry.getEndDate(),
                entry.getIncome(),
                entry.getWorkType().name(),
                entry.getCreatedAt()
        );
    }

    public UUID getEntryId() { return entryId; }
    public UUID getProfileId() { return profileId; }
    public String getStateCode() { return stateCode; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public BigDecimal getIncome() { return income; }
    public String getWorkType() { return workType; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
