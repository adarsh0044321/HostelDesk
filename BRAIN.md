# HostelDesk Engineering Brain & Context Memory

This document stores the immutable principles, architectural guidelines, domain invariants, and technical constraints of the HostelDesk project.

---

## 1. Domain Invariants & Business Rules
1. **User Identity & Roles**:
   - `STUDENT`: Can report issues, track own issues, upload photos, verify resolution, and reopen an issue. Cannot view other students' tickets without scope or access warden/admin APIs.
   - `WARDEN`: Can monitor hostel health pulse, inspect SLA queues, manually assign or re-assign issues to departments/staff, view recurring pattern clusters, and notify team leads.
   - `MAINTENANCE_STAFF`: Can view assigned tasks in `My Work` / `Queue`, accept tasks, log progress notes, upload proof-of-work photos, and submit issues for student verification.
   - `ADMIN`: Global administration, hostel configuration, user provisioning, and department routing rule management.

2. **Issue State Transition Matrix**:
   ```
   [REPORTED]
       │
       ▼
   [AI_ANALYZING]
       │
       ▼
   [ANALYZED]
       │
       ▼
   [ASSIGNED]
       │
       ▼
   [IN_PROGRESS]
       │
       ▼
   [AWAITING_VERIFICATION] ──(Student rejects)──► [REOPENED]
       │                                              │
       │ (Student confirms)                           ▼
       ▼                                         [ASSIGNED]
   [RESOLVED]
   ```
   *Any attempt to jump across invalid states (e.g. REPORTED directly to RESOLVED by a student) MUST throw `InvalidStateTransitionException` (HTTP 400/422).*

3. **Routing Engine Rules**:
   - `PLUMBING` -> Routed to Plumbing Crew
   - `ELECTRICAL` -> Routed to Electrical Crew
   - `CARPENTRY` -> Routed to Carpentry Department
   - `CLEANING` -> Routed to Housekeeping
   - `INTERNET` -> Routed to IT / Network Operations
   - `CIVIL` -> Routed to Civil Maintenance
   - `GENERAL` -> Routed to Warden On-Duty Review

4. **Multi-Tenant Domain Hierarchy & Server-Side Tenant Isolation**:
   - Hierarchy: `Institute` (e.g. `NCH-001`) ➔ `Campus` ➔ `Hostel` ➔ `Block` ➔ `Floor` ➔ `Room` ➔ `Student`.
   - Administrative Hierarchy: `Institute` ➔ `Warden` / `Department` ➔ `Staff` ➔ `Issues`.
   - **Isolation Invariant**: Every User, Hostel, Department, Issue, RoutingRule, and PasswordResetRequest belongs to an `Institute`.
   - JWT tokens encode `instituteId` and `instituteCode`.
   - Cross-tenant data leaks are strictly prevented at repository query level: queries filtering tickets, users, queues, and statistics MUST be constrained by `institute.id`. A student or staff member from Institute A can NEVER view or mutate tickets from Institute B.

5. **Institutional Password Reset Lifecycle**:
   - Students and staff submit a `POST /api/auth/forgot-password` request specifying `instituteCode`, `identifier` (email or roll number), and `contactInfo`.
   - This creates a `PasswordResetRequest` in `PENDING` state tied to the specific `Institute`.
   - Institute Administrators monitor the queue at `GET /api/institute/password-resets`.
   - Administrators execute `POST /api/institute/password-resets/{id}/approve` with a temporary password (or `/reject`), updating the user's BCrypt hash in a secure, isolated transaction.

6. **Security & Session Rules**:
   - Password hashes (`passwordHash`) are NEVER serialized in DTOs.
   - Tokens expire after 24 hours.
   - Student accounts are restricted from authenticating via the Admin application.
   - File uploads must validate MIME type (`image/jpeg`, `image/png`, `image/webp`) and maximum size (10 MB).
   - Hibernate Lazy Proxies (`ByteBuddyInterceptor`) must NEVER be exposed directly to Jackson serializers; entities with lazy associations use `@JsonIgnore` with primitive projection getters.

7. **Native Android Crash Hardening & Performance Invariants**:
   - **XML Inflation Safety**: Layout XML files must contain strictly valid Android View tags (zero unparsed HTML tags like `<div>`). All list screens must declare `layoutLoadingState` (`ProgressBar`), `layoutEmptyState`, and `layoutErrorState` (`btnRetry`).
   - **Single-Flight Idempotency**: Fragments and activities must guard network loading against duplicate triggers (`isFirstLoad` flag) and gracefully redirect to `LoginActivity` on HTTP 401 token expiration.
   - **Camera Lifecycle Safety**: Camera capture photo paths must be preserved across Activity destruction/recreation using `onSaveInstanceState` and restored in `onCreate`.
   - **Bitmap Memory Protection**: Camera captures and gallery selections must never be decoded directly to full-resolution Bitmaps on the UI thread. All image attachments must be downsampled in a background executor using `BitmapFactory.Options.inSampleSize` bounded to a maximum dimension of 1920px with 85% JPEG compression, eliminating `OutOfMemoryError` (OOM).

---

## 2. Technical Stack Specifications
- **Student Android**: Java 17, Android SDK API 34 (Min SDK 24), Retrofit 2.9, OkHttp 4.12, Material Components 1.11, ViewBinding, ViewModel/LiveData.
- **Admin Android**: Java 17, Android SDK API 34 (Min SDK 24), Retrofit 2.9, OkHttp 4.12, Material Components 1.11, ViewBinding, ViewModel/LiveData.
- **Backend**: Java 17, Spring Boot 3.2.3, Spring Security 6, Spring Data JPA, Hibernate 6, Flyway 10, PostgreSQL Driver 42.7.
  - *Compiler Environment Note*: JBR-17 (JetBrains Runtime) requires `-XX:TieredStopAtLevel=1` in `maven-surefire-plugin` to bypass a known C2 JIT compiler bug (`opto/node.hpp:1125`) during ByteBuddy runtime proxy generation.
- **AI Service**: Python 3.11, FastAPI 0.110+, Pydantic v2, Uvicorn, Pytest.
- **Database**: PostgreSQL (Supabase Cloud PostgreSQL pooler in production; embedded H2 compatibility profile for standalone offline testing).

---

## 3. Stitch Design Tokens
- Canvas Background: `#FAF8F5`
- Card Surface: `#FFFFFF`
- Card Border: `#E8E4DD`
- Subtle Container: `#F4F0EB`
- Brand Primary (Terracotta): `#BA5333` (Light) / `#A04022` / `#D46D4A` (Dark)
- Primary Text (Charcoal): `#1A1C1F`
- Secondary Text (Muted): `#67625B`
- Urgent Pill: `#B43B2B` with `#FFF1EB` background
- Sage / Success: `#4A6B5D` with `#E8F0EC` background
- Progress Blue: `#26648E` with `#EDF5FA` background

