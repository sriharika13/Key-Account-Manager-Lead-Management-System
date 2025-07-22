-- Add foreign key constraints and business rules

-- Foreign Key Constraints
ALTER TABLE leads ADD CONSTRAINT fk_leads_kam
    FOREIGN KEY (kam_id) REFERENCES users(id) ON DELETE RESTRICT;

ALTER TABLE contacts ADD CONSTRAINT fk_contacts_lead
    FOREIGN KEY (lead_id) REFERENCES leads(id) ON DELETE CASCADE;

ALTER TABLE interactions ADD CONSTRAINT fk_interactions_lead
    FOREIGN KEY (lead_id) REFERENCES leads(id) ON DELETE RESTRICT;

ALTER TABLE interactions ADD CONSTRAINT fk_interactions_contact
    FOREIGN KEY (contact_id) REFERENCES contacts(id) ON DELETE SET NULL;

ALTER TABLE interactions ADD CONSTRAINT fk_interactions_kam
    FOREIGN KEY (kam_id) REFERENCES users(id) ON DELETE RESTRICT;

ALTER TABLE call_schedule ADD CONSTRAINT fk_call_schedule_kam
    FOREIGN KEY (kam_id) REFERENCES users(id) ON DELETE RESTRICT;

ALTER TABLE call_schedule ADD CONSTRAINT fk_call_schedule_lead
    FOREIGN KEY (lead_id) REFERENCES leads(id) ON DELETE CASCADE;

ALTER TABLE performance_metrics ADD CONSTRAINT fk_performance_lead
    FOREIGN KEY (lead_id) REFERENCES leads(id) ON DELETE CASCADE;

-- Business Rule Constraints
ALTER TABLE leads ADD CONSTRAINT chk_call_frequency
    CHECK (call_frequency > 0 AND call_frequency <= 365);

ALTER TABLE leads ADD CONSTRAINT chk_performance_score
    CHECK (performance_score >= 0 AND performance_score <= 100);

ALTER TABLE interactions ADD CONSTRAINT chk_order_value
    CHECK (order_value >= 0);

ALTER TABLE call_schedule ADD CONSTRAINT chk_priority
    CHECK (priority >= 1 AND priority <= 5);

-- Unique Constraints
ALTER TABLE contacts ADD CONSTRAINT uk_lead_email
    UNIQUE (lead_id, email);

-- Status Check Constraints
ALTER TABLE leads ADD CONSTRAINT chk_lead_status
    CHECK (status IN ('NEW', 'CONTACTED', 'INTERESTED', 'NEGOTIATING', 'CLOSED_WON', 'CLOSED_LOST', 'INACTIVE'));

ALTER TABLE interactions ADD CONSTRAINT chk_interaction_type
    CHECK (type IN ('CALL', 'ORDER', 'EMAIL', 'MEETING'));

ALTER TABLE interactions ADD CONSTRAINT chk_interaction_status
    CHECK (status IN ('COMPLETED', 'PENDING', 'CANCELLED', 'SCHEDULED'));

ALTER TABLE call_schedule ADD CONSTRAINT chk_call_status
    CHECK (status IN ('PENDING', 'COMPLETED', 'NO_ANSWER', 'BUSY', 'RESCHEDULED', 'CANCELLED'));