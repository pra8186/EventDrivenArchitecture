package com.eventdriven.dto.response;

import com.eventdriven.entity.TaxProfile;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for tax profile data. Exposes IDs and values, not full entities.
 */
public class TaxProfileResponse {

    private final UUID profileId;
    private final UUID userId;
    private final String homeStateCode;
    private final int taxYear;
    private final Integer dependentCount;
    private final String filingStatus;
    private final LocalDateTime createdAt;

    private TaxProfileResponse(UUID profileId, UUID userId, String homeStateCode,
                               int taxYear, Integer dependentCount, String filingStatus,
                               LocalDateTime createdAt) {
        this.profileId = profileId;
        this.userId = userId;
        this.homeStateCode = homeStateCode;
        this.taxYear = taxYear;
        this.dependentCount = dependentCount;
        this.filingStatus = filingStatus;
        this.createdAt = createdAt;
    }

    public static TaxProfileResponse from(TaxProfile profile) {
        return new TaxProfileResponse(
                profile.getProfileId(),
                profile.getUser().getUserId(),
                profile.getHomeState().getStateCode(),
                profile.getTaxYear(),
                profile.getDependentCount(),
                profile.getFilingStatus().name(),
                profile.getCreatedAt()
        );
    }

    public UUID getProfileId() { return profileId; }
    public UUID getUserId() { return userId; }
    public String getHomeStateCode() { return homeStateCode; }
    public int getTaxYear() { return taxYear; }
    public Integer getDependentCount() { return dependentCount; }
    public String getFilingStatus() { return filingStatus; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
