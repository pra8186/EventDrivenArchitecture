package com.eventdriven.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class CreateWorkDayEntryRequest {

    @NotNull(message = "Profile ID is required")
    private UUID profileId;

    @NotBlank(message = "State code is required")
    @Size(min = 2, max = 2, message = "State code must be exactly 2 characters")
    private String stateCode;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @NotNull(message = "Income is required")
    @DecimalMin(value = "0.00", message = "Income must be non-negative")
    private BigDecimal income;

    @NotBlank(message = "Work type is required")
    private String workType;

    public UUID getProfileId() { return profileId; }
    public void setProfileId(UUID profileId) { this.profileId = profileId; }
    public String getStateCode() { return stateCode; }
    public void setStateCode(String stateCode) { this.stateCode = stateCode; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public BigDecimal getIncome() { return income; }
    public void setIncome(BigDecimal income) { this.income = income; }
    public String getWorkType() { return workType; }
    public void setWorkType(String workType) { this.workType = workType; }
}
