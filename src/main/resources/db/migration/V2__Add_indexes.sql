-- Add performance-critical indexes

-- User indexes
CREATE INDEX idx_users_email ON users(email);

-- Lead indexes (most important for performance)
CREATE INDEX idx_leads_kam_status ON leads(kam_id, status);
CREATE INDEX idx_leads_call_schedule ON leads(last_call_date, call_frequency);
CREATE INDEX idx_leads_performance ON leads(performance_score DESC);
CREATE INDEX idx_leads_city ON leads(city);
CREATE INDEX idx_leads_created ON leads(created_at DESC);

-- Contact indexes
CREATE INDEX idx_contacts_lead ON contacts(lead_id);
CREATE INDEX idx_contacts_email ON contacts(email);
CREATE INDEX idx_contacts_primary ON contacts(lead_id, is_primary);

-- Interaction indexes (critical for analytics)
CREATE INDEX idx_interactions_lead_date ON interactions(lead_id, interaction_date DESC);
CREATE INDEX idx_interactions_kam_type ON interactions(kam_id, type, interaction_date);
CREATE INDEX idx_interactions_contact ON interactions(contact_id);
CREATE INDEX idx_interactions_follow_up ON interactions(follow_up_date);

-- Call Schedule indexes
CREATE INDEX idx_call_schedule_kam_date ON call_schedule(kam_id, scheduled_date);
CREATE INDEX idx_call_schedule_status ON call_schedule(status, scheduled_date);
CREATE INDEX idx_call_schedule_lead ON call_schedule(lead_id, scheduled_date);

-- Performance Metrics indexes
CREATE INDEX idx_performance_lead_date ON performance_metrics(lead_id, metric_date DESC);
CREATE INDEX idx_performance_date ON performance_metrics(metric_date DESC);
