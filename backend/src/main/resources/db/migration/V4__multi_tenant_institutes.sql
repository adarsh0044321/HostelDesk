-- V4__multi_tenant_institutes.sql: Multi-tenant Institute, Campus, Password Reset Requests

CREATE TABLE IF NOT EXISTS institutes (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(150) NOT NULL,
    type VARCHAR(50) DEFAULT 'UNIVERSITY',
    email VARCHAR(100),
    contact_number VARCHAR(30),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS campuses (
    id BIGSERIAL PRIMARY KEY,
    institute_id BIGINT NOT NULL REFERENCES institutes(id) ON DELETE CASCADE,
    code VARCHAR(50) NOT NULL,
    name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE hostels ADD COLUMN IF NOT EXISTS institute_id BIGINT REFERENCES institutes(id) ON DELETE CASCADE;
ALTER TABLE hostels ADD COLUMN IF NOT EXISTS campus_id BIGINT REFERENCES campuses(id) ON DELETE SET NULL;

ALTER TABLE rooms ADD COLUMN IF NOT EXISTS floor_number VARCHAR(20);

ALTER TABLE departments ADD COLUMN IF NOT EXISTS institute_id BIGINT REFERENCES institutes(id) ON DELETE CASCADE;

ALTER TABLE routing_rules ADD COLUMN IF NOT EXISTS institute_id BIGINT REFERENCES institutes(id) ON DELETE CASCADE;

ALTER TABLE users ADD COLUMN IF NOT EXISTS institute_id BIGINT REFERENCES institutes(id) ON DELETE CASCADE;
ALTER TABLE users ADD COLUMN IF NOT EXISTS campus_id BIGINT REFERENCES campuses(id) ON DELETE SET NULL;
ALTER TABLE users ADD COLUMN IF NOT EXISTS needs_password_change BOOLEAN DEFAULT FALSE;

ALTER TABLE issues ADD COLUMN IF NOT EXISTS institute_id BIGINT REFERENCES institutes(id) ON DELETE CASCADE;
ALTER TABLE issues ADD COLUMN IF NOT EXISTS campus_id BIGINT REFERENCES campuses(id) ON DELETE SET NULL;
ALTER TABLE issues ADD COLUMN IF NOT EXISTS floor_number VARCHAR(20);

CREATE TABLE IF NOT EXISTS password_reset_requests (
    id BIGSERIAL PRIMARY KEY,
    institute_id BIGINT NOT NULL REFERENCES institutes(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    user_role VARCHAR(30) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    reason TEXT,
    temporary_password_hash VARCHAR(255),
    reviewed_by_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    reviewed_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Backfill default institute NCH-001 so existing data is seamless
INSERT INTO institutes (code, name, type, email, contact_number, status)
VALUES ('NCH-001', 'North Campus Housing Institute', 'UNIVERSITY', 'admin@campus.edu', '+1-800-CAMPUS', 'ACTIVE')
ON CONFLICT (code) DO NOTHING;

-- Seed default North Campus
INSERT INTO campuses (institute_id, code, name)
SELECT id, 'NC', 'North Campus' FROM institutes WHERE code = 'NCH-001'
ON CONFLICT DO NOTHING;

-- Associate existing entities with NCH-001
UPDATE hostels SET institute_id = (SELECT id FROM institutes WHERE code = 'NCH-001') WHERE institute_id IS NULL;
UPDATE departments SET institute_id = (SELECT id FROM institutes WHERE code = 'NCH-001') WHERE institute_id IS NULL;
UPDATE routing_rules SET institute_id = (SELECT id FROM institutes WHERE code = 'NCH-001') WHERE institute_id IS NULL;
UPDATE users SET institute_id = (SELECT id FROM institutes WHERE code = 'NCH-001') WHERE institute_id IS NULL;
UPDATE issues SET institute_id = (SELECT id FROM institutes WHERE code = 'NCH-001') WHERE institute_id IS NULL;
