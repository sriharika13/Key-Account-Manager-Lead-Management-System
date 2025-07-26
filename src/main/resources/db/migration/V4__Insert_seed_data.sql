-- Insert initial seed data for testing

-- Insert sample KAMs
INSERT INTO users (id, name, email, timezone, password_hash) VALUES
    ('11111111-1111-1111-1111-111111111111', 'Rajesh Kumar', 'rajesh.kumar@udaan.com', 'Asia/Kolkata', '$2a$10$B1OL8W6yIW1DDeqRt.8KlOK3rVfHVCjBn4gJ3KCt.tDeVnIh80F96'),
    ('22222222-2222-2222-2222-222222222222', 'Priya Sharma', 'priya.sharma@udaan.com', 'Asia/Kolkata', '$2a$10$B1OL8W6yIW1DDeqRt.8KlOK3rVfHVCjBn4gJ3KCt.tDeVnIh80F96'),
    ('33333333-3333-3333-3333-333333333333', 'Amit Singh', 'amit.singh@udaan.com', 'Asia/Kolkata', '$2a$10$B1OL8W6yIW1DDeqRt.8KlOK3rVfHVCjBn4gJ3KCt.tDeVnIh80F96');

-- Insert sample restaurant leads
INSERT INTO leads (id, name, city, cuisine_type, status, kam_id, call_frequency, last_call_date, performance_score) VALUES
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Spice Route Restaurant', 'Mumbai', 'North Indian', 'INTERESTED', '11111111-1111-1111-1111-111111111111', 7, '2024-12-01', 75.50),
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'South Delights', 'Bangalore', 'South Indian', 'NEGOTIATING', '22222222-2222-2222-2222-222222222222', 5, '2024-11-28', 85.25),
    ('cccccccc-cccc-cccc-cccc-cccccccccccc', 'Pizza Corner', 'Delhi', 'Italian', 'NEW', '11111111-1111-1111-1111-111111111111', 14, NULL, 0.00),
    ('dddddddd-dddd-dddd-dddd-dddddddddddd', 'Curry House', 'Pune', 'Multi-cuisine', 'CONTACTED', '33333333-3333-3333-3333-333333333333', 10, '2024-11-25', 45.75);

-- Insert sample contacts
INSERT INTO contacts (id, lead_id, name, role, email, is_primary) VALUES
    ('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Ravi Patel', 'Owner', 'ravi@spiceroute.com', TRUE),
    ('ffffffff-ffff-ffff-ffff-ffffffffffff', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Anita Patel', 'Manager', 'anita@spiceroute.com', FALSE),
    ('123e4567-e89b-12d3-a456-426614174000', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Suresh Reddy', 'Chef', 'suresh@southdelights.com', TRUE);

-- Insert sample interactions
INSERT INTO interactions (id, lead_id, contact_id, kam_id, type, status, interaction_date, order_value) VALUES
    ('88888888-4444-4444-aaaa-123456789abc', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', '11111111-1111-1111-1111-111111111111', 'CALL', 'COMPLETED', '2024-12-01 10:30:00', NULL),
    ('f2c70a00-62c4-4b5a-aaf7-cf843b9a4aa1', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '123e4567-e89b-12d3-a456-426614174000', '22222222-2222-2222-2222-222222222222', 'ORDER', 'COMPLETED', '2024-11-28 14:15:00', 25000.00);

-- Insert sample call schedules
INSERT INTO call_schedule (id, kam_id, lead_id, scheduled_date, status, priority) VALUES
    ('79a531b0-f5c2-4db4-9145-0422fc0d6a30', '11111111-1111-1111-1111-111111111111', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', CURRENT_DATE, 'PENDING', 2),
    ('3bfe8c1e-94c6-460e-ae02-4ea0b5f5c8f6', '33333333-3333-3333-3333-333333333333', 'dddddddd-dddd-dddd-dddd-dddddddddddd', CURRENT_DATE + INTERVAL '1 day', 'PENDING', 1);
