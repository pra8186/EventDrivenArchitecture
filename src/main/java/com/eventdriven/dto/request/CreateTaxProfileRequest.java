package com.eventdriven.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public class CreateTaxProfileRequest {

    @NotNull(message = "User ID is required")
    private UUID userId;

    @NotBlank(message = "State code is required")
    @Size(min = 2, max = 2, message = "State code must be exactly 2 characters")
    private String stateCode;

    @Min(value = 2000, message = "Tax year must be 2000 or later")
    @Max(value = 2100, message = "Tax year must be 2100 or earlier")
    private int taxYear;

    @NotBlank(message = "Filing status is required")
    private String filingStatus;

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public String getStateCode() { return stateCode; }
    public void setStateCode(String stateCode) { this.stateCode = stateCode; }
    public int getTaxYear() { return taxYear; }
    public void setTaxYear(int taxYear) { this.taxYear = taxYear; }
    public String getFilingStatus() { return filingStatus; }
    public void setFilingStatus(String filingStatus) { this.filingStatus = filingStatus; }
}
