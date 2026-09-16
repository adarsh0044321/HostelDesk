# HostelDesk Architecture Specification

## 1. System Philosophy & Objectives
HostelDesk is a native Android-first collegiate housing operations platform. It eliminates the traditional disconnect between students reporting hostel infrastructure issues and the maintenance/warden dispatch teams responsible for resolving them.

The system is architected as a clean modular monolith on the backend with real-time REST integration, persistent SQL storage, deterministic state machine enforcement, and an asynchronous Python AI inference service.

```
+-------------------------------------------------------------------------+
|                              HostelDesk Ecosystem                       |
+-------------------------------------------------------------------------+
       |                                                   |
       | Native Java Android                               | Native Java Android
       v                                                   v
+-----------------------------+             +-----------------------------+
|    HostelDesk Student App   |             |   HostelDesk Admin/Staff    |
| (com.hosteldesk.student)    |             | (com.hosteldesk.admin)      |
| - Resident issue reporting  |             | - Warden dispatch dashboard |
| - AI analysis preview       |             | - Maintenance work order    |
| - Camera / Photo upload     |             | - Progress notes & proof    |
| - Resolution verification   |             | - SLA timers & Analytics    |
+-----------------------------+             +-----------------------------+
               \                                   /
                \                                 /
         HTTPS / REST (JWT Auth, Multipart Uploads)
                  \                             /
                   v                           v
+-------------------------------------------------------------------------+
|                  Java Spring Boot Backend (Port 8080)                   |
|                                                                         |
|  [Security & Auth Layer]                                                |
|   - BCrypt Password Hashing, JWT Token Issuance & Validation            |
|   - Role-Based Access Control (STUDENT, WARDEN, STAFF, ADMIN)           |
|                                                                         |
|  [Controller Layer]                                                     |
|   - StudentIssueController, AdminIssueController, AuthController        |
|   - NotificationController, AnalyticsController, FileController         |
|                                                                         |
|  [Service & Business Logic]                                             |
|   - IssueService (State machine transitions, SLA calculation)           |
|   - RoutingEngine (Rule-based department and staff assignment)          |
|   - NotificationService (In-app event notifications)                    |
|   - AiIntegrationService (FastAPI client with deterministic fallback)   |
|   - FileStorageService (MIME validation, secure file renaming)          |
|                                                                         |
|  [Persistence Layer]                                                    |
|   - Spring Data JPA, Hibernate, Flyway Migrations                       |
+------------------------------------+------------------------------------+
                  |                  |
      HTTP / JSON |                  | JDBC Connection Pool
                  v                  v
+------------------------------------+    +-------------------------------+
|     Python AI Service (Port 8000)  |    |     PostgreSQL Database       |
|                                    |    |                               |
|  - FastAPI + Pydantic              |    |  - users, hostels, blocks     |
|  - Issue classification            |    |  - rooms, departments         |
|  - Urgency & Priority rating       |    |  - routing_rules, issues      |
|  - Root cause summary & safety     |    |  - attachments, ai_analysis   |
|  - Recurring pattern clustering    |    |  - activities, notifications  |
+------------------------------------+    +-------------------------------+
```

---

## 2. Core Architectural Principles
1. **Native Android First**: Both Student and Admin clients are authentic native Android applications written in Java using Android SDK, Jetpack components (ViewModel, LiveData, ViewBinding), and Material Design 3.
2. **Strict Client Segregation**: Two distinct APKs are compiled (`student-android` and `admin-android`). Administrative controls are not compiled into the student binary.
3. **Centralized Authority**: Android clients NEVER communicate directly with PostgreSQL or the Python AI service. The Spring Boot backend enforces all authentication, role-based authorization, and data validation.
4. **Deterministic Issue State Machine**: Issue lifecycle transitions are strictly validated in the backend service layer:
   - `REPORTED` -> `AI_ANALYZING` -> `ANALYZED` -> `ASSIGNED` -> `IN_PROGRESS` -> `AWAITING_VERIFICATION` -> `RESOLVED`
   - If rejected by student: `AWAITING_VERIFICATION` -> `REOPENED` -> `ASSIGNED` -> `IN_PROGRESS` -> `AWAITING_VERIFICATION`
5. **Resilient AI Fallback**: If the Python AI service is unreachable, the backend automatically falls back to an internal deterministic pattern classifier without failing the student's request.
6. **Zero Plaintext Secrets**: Passwords are saved with BCrypt (12 rounds). Tokens are signed with HMAC-SHA256. Database and storage paths are externalized via environment and properties.

---

## 3. Communication Protocols
- **Client to Backend**: RESTful JSON over HTTP(S). Multipart/form-data for image and attachment uploads.
- **Backend to Python AI**: RESTful JSON over HTTP with request timeout and circuit-breaker fallback.
- **Backend to Database**: JDBC connection pool via HikariCP, managed by Spring Data JPA and Flyway migrations.

---

## 4. Production Cloud Deployment Topology

```
   [Native Android Device]
              │
              │ HTTPS / TLS (Port 443)
              ▼
   [Render Cloud Hosted Web Service]
     URL: https://hosteldesk-backend-pc8z.onrender.com
     - Containerized Spring Boot 3 on Eclipse Temurin JDK 17
     - Automatic TLS termination & zero-downtime health probes
     - Connects internally to Supabase pooler over AWS backbone
              │
              │ TLS 1.3 / JDBC (Port 5432)
              ▼
   [Supabase Cloud PostgreSQL]
     Host: aws-0-ap-northeast-2.pooler.supabase.com:5432
     - High-availability PostgreSQL instance
     - 12 normalized tables with FK constraints, indices, and audit timestamps
```

### Key Operational Advantages
1. **Zero PC Dependency**: The system runs 24/7 in the cloud without requiring a local development workstation to remain powered on.
2. **Campus Firewall Immunity**: University Wi-Fi networks frequently block outbound raw database ports (`5432`, `6543`). By routing all mobile traffic through Render over standard HTTPS port `443`, the Android clients operate transparently across any Wi-Fi, hotspot, or cellular connection.
3. **Automated Client Fallbacks**: The Android apps incorporate a dynamic `SessionManager` that seamlessly migrates legacy development IPs to the cloud Render endpoint, while preserving an interactive Server Switcher modal for developer testing.
