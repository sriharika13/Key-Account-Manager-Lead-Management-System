CREATE TABLE users (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    timezone VARCHAR(50) DEFAULT 'UTC',
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE leads (
    id UUID PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    city VARCHAR(100),
    cuisine_type VARCHAR(50),
    status VARCHAR(20) NOT NULL DEFAULT 'NEW',
    kam_id UUID NOT NULL,
    call_frequency INTEGER NOT NULL DEFAULT 7,
    last_call_date DATE,
    performance_score DECIMAL(5,2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE contacts (
    id UUID PRIMARY KEY,
    lead_id UUID NOT NULL,
    name VARCHAR(100) NOT NULL,
    role VARCHAR(50),
    email VARCHAR(150),
    is_primary BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE interactions (
    id UUID PRIMARY KEY,
    lead_id UUID NOT NULL,
    contact_id UUID,
    kam_id UUID NOT NULL,
    type VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    interaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    order_value DECIMAL(10,2),
    follow_up_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE call_schedule (
    id UUID PRIMARY KEY,
    kam_id UUID NOT NULL,
    lead_id UUID NOT NULL,
    scheduled_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    priority INTEGER DEFAULT 3,
    next_scheduled_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE performance_metrics (
    id UUID PRIMARY KEY,
    lead_id UUID NOT NULL,
    metric_date DATE NOT NULL,
    metric_value DECIMAL(15,2) NOT NULL,
    target_value DECIMAL(15,2),
    period_type VARCHAR(20) DEFAULT 'DAILY',
    calculated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
