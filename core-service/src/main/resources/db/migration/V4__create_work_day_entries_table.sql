CREATE TABLE work_day_entries (
    entry_id UUID PRIMARY KEY DEFAULT gen_random_uuid() NOT NULL,
    profile_id UUID NOT NULL,
    state_code VARCHAR(2) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    income DECIMAL NOT NULL,
    work_type VARCHAR NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT fk_work_day_entries_profile
        FOREIGN KEY (profile_id) REFERENCES tax_profiles (profile_id) ON DELETE RESTRICT,
    CONSTRAINT fk_work_day_entries_state
        FOREIGN KEY (state_code) REFERENCES states (state_code) ON DELETE RESTRICT,
    CONSTRAINT chk_work_day_entries_income CHECK (income >= 0),
    CONSTRAINT chk_work_day_entries_work_type
        CHECK (work_type IN ('REMOTE', 'ONSITE', 'CONFERENCE', 'RELOCATION'))
);

CREATE INDEX idx_work_day_entries_profile ON work_day_entries (profile_id);
CREATE INDEX idx_work_day_entries_state ON work_day_entries (state_code);
CREATE INDEX idx_work_day_entries_profile_state ON work_day_entries (profile_id, state_code);
