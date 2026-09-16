-- ==========================================================
-- HostelDesk PostgreSQL Master Seed Data (Corrected)
-- ==========================================================

-- 1. Hostels
INSERT INTO hostels (id, name, location, description, active)
VALUES 
(1, 'Tagore Hall', 'North Campus', 'Primary undergraduate residence', true),
(2, 'Shastri Hall', 'North Campus', 'Postgraduate residence', true)
ON CONFLICT (id) DO NOTHING;

-- 2. Blocks
INSERT INTO blocks (id, hostel_id, name)
VALUES 
(1, 1, 'Block A'),
(2, 1, 'Block B'),
(3, 2, 'Block A')
ON CONFLICT (id) DO NOTHING;

-- 3. Rooms
INSERT INTO rooms (id, block_id, room_number, capacity)
VALUES 
(1, 2, '201', 2),
(2, 2, '202', 2),
(3, 2, '203', 2),
(4, 2, '204', 2),
(5, 2, '205', 2),
(6, 2, '206', 2),
(7, 1, '312', 2)
ON CONFLICT (id) DO NOTHING;

-- 4. Departments
INSERT INTO departments (id, name, display_name, description, active)
VALUES 
(1, 'PLUMBING', 'Plumbing & Water Supply', 'Water leaks, taps, drainage, and geyser maintenance', true),
(2, 'ELECTRICAL', 'Electrical & Power Operations', 'Power sockets, lighting, fans, and wiring safety', true),
(3, 'CARPENTRY', 'Carpentry & Furniture', 'Beds, desks, locks, doors, and window repairs', true),
(4, 'CLEANING', 'Housekeeping & Sanitation', 'Corridor, washroom, and common area sanitation', true),
(5, 'INTERNET', 'IT & Campus Network', 'Wi-Fi access points, LAN ports, and connectivity', true),
(6, 'CIVIL', 'Civil Infrastructure', 'Masonry, roof dampness, plaster, and painting', true),
(7, 'GENERAL', 'General Operations & Warden Desk', 'General hostel complaints and administrative issues', true)
ON CONFLICT (id) DO NOTHING;

-- 5. Routing Rules
INSERT INTO routing_rules (id, category, department_id, default_priority, active)
VALUES 
(1, 'PLUMBING', 1, 'P2_HIGH', true),
(2, 'ELECTRICAL', 2, 'P1_URGENT', true),
(3, 'CARPENTRY', 3, 'P3_MEDIUM', true),
(4, 'CLEANING', 4, 'P3_MEDIUM', true),
(5, 'INTERNET', 5, 'P3_MEDIUM', true),
(6, 'CIVIL', 6, 'P3_MEDIUM', true),
(7, 'GENERAL', 7, 'P3_MEDIUM', true)
ON CONFLICT (id) DO NOTHING;

-- 6. Users (Passwords: student123, warden123, staff123, admin123)
INSERT INTO users (id, full_name, email, phone, institutional_id, password_hash, role, status, hostel_id, department_id, room_number)
VALUES
(1, 'Aarav Patel', 'aarav@campus.edu', '+91 98765 43210', 'ST-8819', '$2a$10$/ra67b8pdcMSUJrRxbLedOAcqFLBO35Q5tJMouZNbYyGM43u5llsy', 'STUDENT', 'ACTIVE', 1, null, '204'),
(2, 'Warden R. Sharma', 'warden.sharma@campus.edu', '+91 98765 00001', 'WR-1001', '$2a$10$GQ/RxAuIPVSV/MeBaUNC6.Hj0W42SwmS8ytUBwOkF0wKSWUKLVbj6', 'WARDEN', 'ACTIVE', 1, null, null),
(3, 'Suresh Kumar', 'suresh@campus.edu', '+91 98765 11112', 'STF-201', '$2a$10$y3Q/xzSnrzbSGPJVFvm.ZOO7ylPUgNtFLuIETemHwFOxGmzY.cQ.W', 'MAINTENANCE_STAFF', 'ACTIVE', 1, 1, null),
(4, 'System Administrator', 'admin@campus.edu', '+91 98765 99999', 'ADM-001', '$2a$10$dlLodEsnCvmbubrZVtAHBetLXe2m8IVoNtW5ZqS0p7oYmuZ71xil2', 'ADMIN', 'ACTIVE', null, null, null)
ON CONFLICT (id) DO NOTHING;

-- 7. Initial Reference Issues
INSERT INTO issues (id, ticket_number, title, description, category, priority, status, reported_by_id, assigned_staff_id, assigned_department_id, hostel_id, block_name, room_number, created_at, resolved_at)
VALUES
(1, 'HD-1042', 'Bathroom ceiling water leakage', 'Continuous water dripping from the ceiling above shower area.', 'PLUMBING', 'P2_HIGH', 'RESOLVED', 1, 3, 1, 1, 'Block B', '204', NOW() - INTERVAL '2 days', NOW() - INTERVAL '4 hours'),
(2, 'HD-1038', 'Study desk drawer runner damaged', 'Right side study desk drawer stuck and won''t close.', 'CARPENTRY', 'P3_MEDIUM', 'IN_PROGRESS', 1, null, 3, 1, 'Block B', '204', NOW() - INTERVAL '1 day', null),
(3, 'HD-4819', 'Switchboard sparking when charging laptop', 'Noticeable electric spark and crackling sound near desk switchboard.', 'ELECTRICAL', 'P1_URGENT', 'ASSIGNED', 1, null, 2, 1, 'Block B', '204', NOW() - INTERVAL '3 hours', null)
ON CONFLICT (id) DO NOTHING;

-- 8. AI Analysis for Issues
INSERT INTO issue_ai_analysis (issue_id, detected_category, detected_priority, recommended_department, summary, safety_hazard_note, confidence, is_fallback)
VALUES
(1, 'PLUMBING', 'P2_HIGH', 'PLUMBING', 'Ceiling pipe leakage near shower fixture', 'Risk of slippery floor and dampness', 0.940, false),
(2, 'CARPENTRY', 'P3_MEDIUM', 'CARPENTRY', 'Study desk mechanical runner failure', 'No immediate life safety risk', 0.880, false),
(3, 'ELECTRICAL', 'P1_URGENT', 'ELECTRICAL', 'Severe arcing and spark hazard on power socket', 'DANGER: Fire hazard and electric shock risk. Keep switch off.', 0.975, false)
ON CONFLICT (issue_id) DO NOTHING;

-- 9. Issue Activities
INSERT INTO issue_activities (issue_id, performed_by_id, action, message)
VALUES
(1, 1, 'REPORTED', 'Issue submitted by Aarav Patel'),
(1, 2, 'ASSIGNED', 'Assigned to Suresh Kumar (Plumbing Team)'),
(1, 3, 'RESOLVED', 'Overhead ceiling joint replaced and tested. Ready for student verification.'),
(2, 1, 'REPORTED', 'Reported by Aarav Patel'),
(3, 1, 'REPORTED', 'URGENT: Electrical sparking reported. AI auto-elevated priority.');

-- Reset auto-increment sequences for future tickets and users
SELECT setval('hostels_id_seq', (SELECT COALESCE(MAX(id), 1) FROM hostels));
SELECT setval('blocks_id_seq', (SELECT COALESCE(MAX(id), 1) FROM blocks));
SELECT setval('rooms_id_seq', (SELECT COALESCE(MAX(id), 1) FROM rooms));
SELECT setval('departments_id_seq', (SELECT COALESCE(MAX(id), 1) FROM departments));
SELECT setval('routing_rules_id_seq', (SELECT COALESCE(MAX(id), 1) FROM routing_rules));
SELECT setval('users_id_seq', (SELECT COALESCE(MAX(id), 1) FROM users));
SELECT setval('issues_id_seq', (SELECT COALESCE(MAX(id), 1) FROM issues));
