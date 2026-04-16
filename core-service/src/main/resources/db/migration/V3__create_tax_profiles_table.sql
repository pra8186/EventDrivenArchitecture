CREATE TABLE tax_profiles (
    profile_id UUID PRIMARY KEY DEFAULT gen_random_uuid() NOT NULL,
    user_id UUID NOT NULL,
    home_state_code VARCHAR(2) NOT NULL,
    tax_year INTEGER NOT NULL,
    dependent_count INTEGER DEFAULT 0,
    filing_status VARCHAR NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT fk_tax_profiles_user
        FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE RESTRICT,
    CONSTRAINT fk_tax_profiles_state
        FOREIGN KEY (home_state_code) REFERENCES states (state_code) ON DELETE RESTRICT,
    CONSTRAINT chk_tax_profiles_filing_status
        CHECK (filing_status IN ('SINGLE', 'MFJ', 'MFS', 'HOH'))
);

CREATE INDEX idx_tax_profiles_user ON tax_profiles (user_id);
CREATE INDEX idx_tax_profiles_year ON tax_profiles (tax_year);
CREATE UNIQUE INDEX idx_tax_profiles_user_year ON tax_profiles (user_id, tax_year);
