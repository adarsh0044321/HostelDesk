-- V3__seed_data.sql: Reference Hostels, Blocks, Rooms, Departments, Routing Rules

INSERT INTO hostels (id, name, location, description, active)
VALUES 
(1, 'Tagore Hall', 'North Campus', 'Primary undergraduate residence', true),
(2, 'Shastri Hall', 'North Campus', 'Postgraduate residence', true)
ON CONFLICT (id) DO NOTHING;

INSERT INTO blocks (id, hostel_id, name)
VALUES 
(1, 1, 'Block A'),
(2, 1, 'Block B'),
(3, 2, 'Block A')
ON CONFLICT (id) DO NOTHING;

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

INSERT INTO departments (id, name, display_name, description, active)
VALUES 
(1, 'PLUMBING', 'Plumbing & Water Supply', 'Handles leaks, fixtures, taps, drainage, and geyser maintenance', true),
(2, 'ELECTRICAL', 'Electrical & Power Operations', 'Handles power, sockets, lighting, fans, and wiring safety', true),
(3, 'CARPENTRY', 'Carpentry & Furniture', 'Handles beds, desks, locks, doors, and window repairs', true),
(4, 'CLEANING', 'Housekeeping & Sanitation', 'Handles corridor, common washroom, and room waste sanitation', true),
(5, 'INTERNET', 'IT & Campus Network', 'Handles Wi-Fi access points, LAN ports, and network equipment', true),
(6, 'CIVIL', 'Civil Infrastructure', 'Handles masonry, roof dampness, plaster, and painting', true),
(7, 'GENERAL', 'General Operations & Warden Desk', 'Handles unclassified complaints and complex issues requiring warden review', true)
ON CONFLICT (id) DO NOTHING;

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
