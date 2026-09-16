# HostelDesk Database Specification (PostgreSQL)

## 1. Schema Overview & Relational Design
HostelDesk relies on a clean, normalized relational model designed for PostgreSQL 14+.

### Primary Tables
1. **`users`**
   - `id` BIGSERIAL PRIMARY KEY
   - `full_name` VARCHAR(100) NOT NULL
   - `email` VARCHAR(100) UNIQUE NOT NULL
   - `phone` VARCHAR(20)
   - `institutional_id` VARCHAR(50) UNIQUE NOT NULL (e.g. `ST-8819`)
   - `password_hash` VARCHAR(255) NOT NULL
   - `role` VARCHAR(30) NOT NULL (`STUDENT`, `WARDEN`, `MAINTENANCE_STAFF`, `ADMIN`)
   - `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' (`ACTIVE`, `SUSPENDED`, `INACTIVE`)
   - `hostel_id` BIGINT REFERENCES `hostels`(id)
   - `room_number` VARCHAR(20)
   - `created_at` TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
   - `updated_at` TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
   - `last_login_at` TIMESTAMP WITH TIME ZONE

2. **`hostels`**
   - `id` BIGSERIAL PRIMARY KEY
   - `name` VARCHAR(100) NOT NULL (e.g. `Tagore Hall`)
   - `location` VARCHAR(100) NOT NULL (e.g. `North Campus`)
   - `description` TEXT
   - `active` BOOLEAN DEFAULT TRUE

3. **`blocks`**
   - `id` BIGSERIAL PRIMARY KEY
   - `hostel_id` BIGINT NOT NULL REFERENCES `hostels`(id) ON DELETE CASCADE
   - `name` VARCHAR(50) NOT NULL (e.g. `Block B`)

4. **`rooms`**
   - `id` BIGSERIAL PRIMARY KEY
   - `block_id` BIGINT NOT NULL REFERENCES `blocks`(id) ON DELETE CASCADE
   - `room_number` VARCHAR(20) NOT NULL (e.g. `204`)
   - `capacity` INT DEFAULT 2

5. **`departments`**
   - `id` BIGSERIAL PRIMARY KEY
   - `name` VARCHAR(50) UNIQUE NOT NULL (`PLUMBING`, `ELECTRICAL`, `CARPENTRY`, `CLEANING`, `INTERNET`, `CIVIL`, `GENERAL`)
   - `display_name` VARCHAR(100) NOT NULL
   - `description` VARCHAR(255)

6. **`routing_rules`**
   - `id` BIGSERIAL PRIMARY KEY
   - `category` VARCHAR(50) NOT NULL
   - `department_id` BIGINT NOT NULL REFERENCES `departments`(id)
   - `default_priority` VARCHAR(20) NOT NULL DEFAULT 'MEDIUM'
   - `active` BOOLEAN DEFAULT TRUE

7. **`issues`**
   - `id` BIGSERIAL PRIMARY KEY
   - `ticket_number` VARCHAR(50) UNIQUE NOT NULL (e.g. `HD-1042`)
   - `reported_by_id` BIGINT NOT NULL REFERENCES `users`(id)
   - `hostel_id` BIGINT NOT NULL REFERENCES `hostels`(id)
   - `block_name` VARCHAR(50) NOT NULL
   - `room_number` VARCHAR(20) NOT NULL
   - `category` VARCHAR(50) NOT NULL
   - `title` VARCHAR(200) NOT NULL
   - `description` TEXT NOT NULL
   - `priority` VARCHAR(20) NOT NULL (`P1_URGENT`, `P2_HIGH`, `P3_MEDIUM`, `P4_LOW`)
   - `status` VARCHAR(30) NOT NULL (`REPORTED`, `AI_ANALYZING`, `ANALYZED`, `ASSIGNED`, `IN_PROGRESS`, `AWAITING_VERIFICATION`, `RESOLVED`, `REOPENED`)
   - `assigned_department_id` BIGINT REFERENCES `departments`(id)
   - `assigned_staff_id` BIGINT REFERENCES `users`(id)
   - `technician_notes` TEXT
   - `resolution_notes` TEXT
   - `reopen_reason` TEXT
   - `created_at` TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
   - `updated_at` TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
   - `resolved_at` TIMESTAMP WITH TIME ZONE
   - `verified_at` TIMESTAMP WITH TIME ZONE

8. **`issue_attachments`**
   - `id` BIGSERIAL PRIMARY KEY
   - `issue_id` BIGINT NOT NULL REFERENCES `issues`(id) ON DELETE CASCADE
   - `file_url` VARCHAR(500) NOT NULL
   - `file_name` VARCHAR(255) NOT NULL
   - `file_type` VARCHAR(50) NOT NULL (e.g. `image/jpeg`)
   - `file_size` BIGINT NOT NULL
   - `attachment_type` VARCHAR(30) NOT NULL DEFAULT 'STUDENT_REPORT' (`STUDENT_REPORT`, `STAFF_COMPLETION_PROOF`)
   - `created_at` TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP

9. **`issue_ai_analysis`**
   - `id` BIGSERIAL PRIMARY KEY
   - `issue_id` BIGINT UNIQUE NOT NULL REFERENCES `issues`(id) ON DELETE CASCADE
   - `detected_category` VARCHAR(50) NOT NULL
   - `detected_priority` VARCHAR(20) NOT NULL
   - `recommended_department` VARCHAR(50) NOT NULL
   - `summary` TEXT NOT NULL
   - `safety_hazard_note` TEXT
   - `confidence` NUMERIC(4,3) NOT NULL
   - `is_fallback` BOOLEAN DEFAULT FALSE
   - `analyzed_at` TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP

10. **`issue_activities`**
    - `id` BIGSERIAL PRIMARY KEY
    - `issue_id` BIGINT NOT NULL REFERENCES `issues`(id) ON DELETE CASCADE
    - `performed_by_id` BIGINT REFERENCES `users`(id)
    - `action` VARCHAR(50) NOT NULL
    - `message` TEXT NOT NULL
    - `created_at` TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP

11. **`notifications`**
    - `id` BIGSERIAL PRIMARY KEY
    - `user_id` BIGINT NOT NULL REFERENCES `users`(id) ON DELETE CASCADE
    - `title` VARCHAR(150) NOT NULL
    - `message` TEXT NOT NULL
    - `type` VARCHAR(50) NOT NULL
    - `related_issue_id` BIGINT REFERENCES `issues`(id)
    - `is_read` BOOLEAN DEFAULT FALSE
    - `created_at` TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP

12. **`infrastructure_insights`**
    - `id` BIGSERIAL PRIMARY KEY
    - `hostel_id` BIGINT REFERENCES `hostels`(id)
    - `block_name` VARCHAR(50)
    - `category` VARCHAR(50) NOT NULL
    - `complaint_count` INT NOT NULL
    - `time_window_days` INT NOT NULL
    - `pattern_description` TEXT NOT NULL
    - `probable_cause` TEXT NOT NULL
    - `recommended_action` TEXT NOT NULL
    - `created_at` TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP

---

## 2. Seed Data Strategy
Flyway migration `V3__seed_data.sql` and `database/seed.sql` pre-seeds:
- 2 Hostels: `Tagore Hall` (North Campus), `Shastri Hall`
- 3 Blocks: `Block A`, `Block B`
- 7 Rooms: `201`, `202`, `203`, `204`, `205`, `206`, `312`
- 7 Standard departments and routing rules (`PLUMBING`, `ELECTRICAL`, `CARPENTRY`, `CLEANING`, `INTERNET`, `CIVIL`, `GENERAL`)
- 4 Seed Users with BCrypt-hashed passwords:
  - Student: `aarav@campus.edu` (`student123`)
  - Warden: `warden.sharma@campus.edu` (`warden123`)
  - Maintenance Staff: `suresh@campus.edu` (`staff123`)
  - Admin: `admin@campus.edu` (`admin123`)
- Initial reference tickets (`#HD-1042`, `#HD-1038`, `#HD-4819`) with AI analyses and timeline activities.

---

## 3. Supabase Cloud PostgreSQL Integration
- **Platform**: Hosted PostgreSQL on Supabase AWS
- **Project Ref**: `qxdhyoylzdaagvtlymhd`
- **Region**: AWS `ap-northeast-2` (Seoul)
- **Session Pooler Host**: `aws-0-ap-northeast-2.pooler.supabase.com`
- **Port**: `5432`
- **Database**: `postgres`
- **Username**: `postgres.qxdhyoylzdaagvtlymhd`
- **SSL Mode**: `require`
- **Connection Management**:
  - Automatically pooled via HikariCP in Spring Boot (`maximum-pool-size: 5`)
  - Direct connection to cloud database by Render backend service in AWS backbone without campus network firewall limitations.

