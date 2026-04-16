CREATE TABLE event_log (
    event_id UUID PRIMARY KEY DEFAULT gen_random_uuid() NOT NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT now(),
    user_id VARCHAR NOT NULL,
    resource_id VARCHAR NOT NULL,
    event_type VARCHAR NOT NULL,
    details TEXT
);

CREATE INDEX idx_event_log_user ON event_log (user_id);
CREATE INDEX idx_event_log_type ON event_log (event_type);
