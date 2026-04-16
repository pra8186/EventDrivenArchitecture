package com.eventdriven.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "work_day_entries", indexes = {
        @Index(name = "idx_work_day_entries_profile", columnList = "profile_id"),
        @Index(name = "idx_work_day_entries_state", columnList = "state_code"),
        @Index(name = "idx_work_day_entries_profile_state", columnList = "profile_id, state_code")
})
public class WorkDayEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "entry_id", nullable = false, updatable = false)
    private UUID entryId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "profile_id", nullable = false)
    private TaxProfile taxProfile;

    @ManyToOne(optional = false)
    @JoinColumn(name = "state_code", referencedColumnName = "state_code", nullable = false)
    private State state;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "income", nullable = false)
    private BigDecimal income;

    @Enumerated(EnumType.STRING)
    @Column(name = "work_type", nullable = false)
    private WorkType workType;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { this.createdAt = LocalDateTime.now(); }

    protected WorkDayEntry() {}

    public WorkDayEntry(TaxProfile taxProfile, State state, LocalDate startDate,
                        LocalDate endDate, BigDecimal income, WorkType workType) {
        this.taxProfile = taxProfile;
        this.state = state;
        this.startDate = startDate;
        this.endDate = endDate;
        this.income = income;
        this.workType = workType;
    }

    public UUID getEntryId() { return entryId; }
    public TaxProfile getTaxProfile() { return taxProfile; }
    public void setTaxProfile(TaxProfile taxProfile) { this.taxProfile = taxProfile; }
    public State getState() { return state; }
    public void setState(State state) { this.state = state; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public BigDecimal getIncome() { return income; }
    public void setIncome(BigDecimal income) { this.income = income; }
    public WorkType getWorkType() { return workType; }
    public void setWorkType(WorkType workType) { this.workType = workType; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
