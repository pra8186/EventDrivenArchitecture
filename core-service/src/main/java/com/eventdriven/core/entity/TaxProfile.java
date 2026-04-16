package com.eventdriven.core.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tax_profiles",
        uniqueConstraints = @UniqueConstraint(name = "idx_tax_profiles_user_year", columnNames = {"user_id", "tax_year"}),
        indexes = {
                @Index(name = "idx_tax_profiles_user", columnList = "user_id"),
                @Index(name = "idx_tax_profiles_year", columnList = "tax_year")
        })
public class TaxProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "profile_id", nullable = false, updatable = false)
    private UUID profileId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "home_state_code", referencedColumnName = "state_code", nullable = false)
    private State homeState;

    @Column(name = "tax_year", nullable = false)
    private int taxYear;

    @Column(name = "dependent_count")
    private Integer dependentCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "filing_status", nullable = false)
    private FilingStatus filingStatus;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { this.createdAt = LocalDateTime.now(); }

    protected TaxProfile() {}

    public TaxProfile(User user, State homeState, int taxYear, FilingStatus filingStatus) {
        this.user = user;
        this.homeState = homeState;
        this.taxYear = taxYear;
        this.filingStatus = filingStatus;
    }

    public UUID getProfileId() { return profileId; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public State getHomeState() { return homeState; }
    public void setHomeState(State homeState) { this.homeState = homeState; }
    public int getTaxYear() { return taxYear; }
    public void setTaxYear(int taxYear) { this.taxYear = taxYear; }
    public Integer getDependentCount() { return dependentCount; }
    public void setDependentCount(Integer dependentCount) { this.dependentCount = dependentCount; }
    public FilingStatus getFilingStatus() { return filingStatus; }
    public void setFilingStatus(FilingStatus filingStatus) { this.filingStatus = filingStatus; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
