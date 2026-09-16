-- V6__batch_and_warden_audits.sql

ALTER TABLE users ADD COLUMN IF NOT EXISTS batch VARCHAR(50);
ALTER TABLE issues ADD COLUMN IF NOT EXISTS warden_view_count INT DEFAULT 0;
ALTER TABLE issues ADD COLUMN IF NOT EXISTS warden_viewed_at TIMESTAMP WITH TIME ZONE;

-- Seed default batch for existing students
UPDATE users SET batch = 'Batch 2026' WHERE role = 'STUDENT' AND (batch IS NULL OR batch = '');
