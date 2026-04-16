package com.eventdriven.core.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "states")
public class State {

    @Id
    @Column(name = "state_code", nullable = false, length = 2)
    private String stateCode;

    @Column(name = "state_name", nullable = false)
    private String stateName;

    protected State() {}

    public State(String stateCode, String stateName) {
        this.stateCode = stateCode;
        this.stateName = stateName;
    }

    public String getStateCode() { return stateCode; }
    public void setStateCode(String stateCode) { this.stateCode = stateCode; }
    public String getStateName() { return stateName; }
    public void setStateName(String stateName) { this.stateName = stateName; }
}
