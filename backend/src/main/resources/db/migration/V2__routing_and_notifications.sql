-- V2__routing_and_notifications.sql: Routing rules, Issues, Attachments, AI Analysis, Activities, Notifications, Insights

CREATE TABLE IF NOT EXISTS routing_rules (
    id BIGSERIAL PRIMARY KEY,
    category VARCHAR(50) NOT NULL,
    department_id BIGINT NOT NULL REFERENCES departments(id) ON DELETE CASCADE,
    default_priority VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS issues (
    id BIGSERIAL PRIMARY KEY,
    ticket_number VARCHAR(50) UNIQUE NOT NULL,
    reported_by_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    hostel_id BIGINT NOT NULL REFERENCES hostels(id) ON DELETE CASCADE,
    block_name VARCHAR(50) NOT NULL,
    room_number VARCHAR(20) NOT NULL,
    category VARCHAR(50) NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    priority VARCHAR(20) NOT NULL DEFAULT 'P3_MEDIUM',
    status VARCHAR(30) NOT NULL DEFAULT 'REPORTED',
    assigned_department_id BIGINT REFERENCES departments(id) ON DELETE SET NULL,
    assigned_staff_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    technician_notes TEXT,
    resolution_notes TEXT,
    reopen_reason TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    resolved_at TIMESTAMP WITH TIME ZONE,
    verified_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE IF NOT EXISTS issue_attachments (
    id BIGSERIAL PRIMARY KEY,
    issue_id BIGINT NOT NULL REFERENCES issues(id) ON DELETE CASCADE,
    file_url VARCHAR(500) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_type VARCHAR(50) NOT NULL,
    file_size BIGINT NOT NULL,
    attachment_type VARCHAR(30) NOT NULL DEFAULT 'STUDENT_REPORT',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS issue_ai_analysis (
    id BIGSERIAL PRIMARY KEY,
    issue_id BIGINT UNIQUE NOT NULL REFERENCES issues(id) ON DELETE CASCADE,
    detected_category VARCHAR(50) NOT NULL,
    detected_priority VARCHAR(20) NOT NULL,
    recommended_department VARCHAR(50) NOT NULL,
    summary TEXT NOT NULL,
    safety_hazard_note TEXT,
    confidence NUMERIC(4,3) NOT NULL,
    is_fallback BOOLEAN DEFAULT FALSE,
    analyzed_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS issue_activities (
    id BIGSERIAL PRIMARY KEY,
    issue_id BIGINT NOT NULL REFERENCES issues(id) ON DELETE CASCADE,
    performed_by_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    action VARCHAR(50) NOT NULL,
    message TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS notifications (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(150) NOT NULL,
    message TEXT NOT NULL,
    type VARCHAR(50) NOT NULL,
    related_issue_id BIGINT REFERENCES issues(id) ON DELETE CASCADE,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS infrastructure_insights (
    id BIGSERIAL PRIMARY KEY,
    hostel_id BIGINT REFERENCES hostels(id) ON DELETE CASCADE,
    block_name VARCHAR(50),
    category VARCHAR(50) NOT NULL,
    complaint_count INT NOT NULL,
    time_window_days INT NOT NULL,
    pattern_description TEXT NOT NULL,
    probable_cause TEXT NOT NULL,
    recommended_action TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);
