-- V5__emergency_contacts_and_announcements.sql

ALTER TABLE institutes ADD COLUMN IF NOT EXISTS ambulance_contact VARCHAR(50);
ALTER TABLE institutes ADD COLUMN IF NOT EXISTS security_contact VARCHAR(50);
ALTER TABLE institutes ADD COLUMN IF NOT EXISTS emergency_desk_contact VARCHAR(50);

CREATE TABLE IF NOT EXISTS announcements (
    id BIGSERIAL PRIMARY KEY,
    institute_id BIGINT NOT NULL REFERENCES institutes(id) ON DELETE CASCADE,
    hostel_id BIGINT REFERENCES hostels(id) ON DELETE CASCADE,
    author_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    author_name VARCHAR(100),
    author_role VARCHAR(50),
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    pinned BOOLEAN NOT NULL DEFAULT FALSE,
    expires_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_announcements_institute_expires ON announcements(institute_id, expires_at);
CREATE INDEX IF NOT EXISTS idx_announcements_hostel ON announcements(hostel_id);
