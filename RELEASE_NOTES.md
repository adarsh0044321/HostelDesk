# HostelDesk v1.5.0 — Official Platform Release

Welcome to the official production release of **HostelDesk**, a comprehensive, multi-tenant collegiate housing issue-management and operations platform designed for university residences, student dormitories, and campus facility management.

---

## 📦 Release Downloads

Directly install the native Android applications on Android 8.0+ (API 26+) devices:

| Application | Target Audience | Package Name | APK Asset |
| :--- | :--- | :--- | :--- |
| **HostelDesk Student** | Resident Students | `com.adarshsingh.hosteldesk` | [HostelDesk-Student-v1.5.apk](https://github.com/adarsh0044321/HostelDesk/releases/download/v1.5.0/HostelDesk-Student-v1.5.apk) |
| **HostelDesk Admin** | Wardens, Staff, Directors | `com.adarshsingh.adminhosteldesk` | [HostelDesk-Admin-v1.5.apk](https://github.com/adarsh0044321/HostelDesk/releases/download/v1.5.0/HostelDesk-Admin-v1.5.apk) |

---

## 🌟 What's New in v1.5.0

### 🎓 1. Student Resident Android Experience
- **AI-Assisted Issue Reporting**: Real-time NLP category recommendation, safety hazard alert banner, and automatic priority rating.
- **Proof Photo Attachments**: Seamless native camera capture via secure `FileProvider` and gallery image picker with thumbnail previews.
- **Resolution Verification Loop**:
  - When technicians complete repairs, residents receive an in-app verification banner.
  - Tapping **"Yes, It's Fixed!"** opens an inspection dialog with a 5-star technician rating and written review.
  - Tapping **"No, Still Broken"** allows residents to submit a reason, automatically transitioning the ticket to `REOPENED` and alerting the warden.
- **Material 3 Layout & Polish**: Full 48dp touch targets, zero text truncation, consistent padding, and refined terracotta palette (`#BA5333`).

### 🛡️ 2. Admin & Staff Operational Portal
- **Executive Portal (4-Factor Authentication)**:
  - Strict role boundary: Institute Administrators (`INSTITUTE_ADMIN`, `SUPER_ADMIN`) must log in exclusively through the Executive Portal.
  - Requires all 4 factors: **Institute Code**, **Official Email/ID**, **Master Password**, and **Executive Security PIN**.
- **Institute Registration with Custom/Auto-Generated PIN**:
  - Provision new institutions with auto-generated 6-digit PINs (or custom codes) stored directly on the Institute entity.
- **Warden Command Center**:
  - Live Residence Health Pulse percentage.
  - Attention-required escalation queues, multi-department technician dispatch matrix, and audit counts.
- **Maintenance Staff Workspace**:
  - Filtered task queues: `My Work`, `Dept Queue`, and `Completed`.
  - One-tap work start (`IN_PROGRESS`) and photographic completion proof upload.
- **Universal Multi-Status Filters**:
  - Filter by `Submitted`, `Assigned`, `In Progress`, `Resolved`, and `Closed` with verified tickets immediately visible in the closed archive.

### ⚡ 3. Java Spring Boot 3 Backend
- **Multi-Tenant Data Isolation**: Complete tenant isolation by institute code across hostels, campuses, users, issues, and departments.
- **Deterministic State Machine**: Strict state validation enforcing `REPORTED` → `ASSIGNED` → `IN_PROGRESS` → `AWAITING_VERIFICATION` → `VERIFIED` transitions.
- **Automated Routing Engine**: Rule-based dispatch mapping issue categories to specialized departments with automated fallbacks.
- **Comprehensive API Suite**: Full REST API documented with clear DTO schemas, role-based endpoint guards (`@PreAuthorize`), and JWT authentication.

### 🤖 4. Python FastAPI AI Service
- **NLP Categorization & Priority Scoring**: Automatic issue categorization with confidence scoring.
- **Recurring Issue Pattern Detector**: Cross-room cluster analysis detecting infrastructure failures (e.g. repeated plumbing issues in the same wing).

---

## 🔑 Pre-Seeded Live Credentials

The backend is live on Render (`https://hosteldesk-backend-pc8z.onrender.com`). You can test immediately using these accounts:

### Institute: `JAI`
- **Executive Admin**: `adminjai` / `adminjai` | **Security PIN**: `112233`
- **Warden**: `warden.jai@campus.edu` / `warden123`
- **Student**: Onboard via Admin or register in app

### Institute: `NCH-001` (North Campus Housing)
- **Executive Admin**: `admin@campus.edu` / `admin123` | **Security PIN**: `112233`
- **Warden**: `warden.sharma@campus.edu` / `warden123`
- **Maintenance Staff**: `suresh@campus.edu` / `staff123` (Plumbing)
- **Student**: `aarav@campus.edu` / `student123` (Room 204, Tagore Hall)

---

## 🛠️ Verification & Test Suite
- **Backend Unit & Integration Tests**: 16/16 tests passing (`mvn test` clean exit code 0).
- **Android Builds**: Assembled with Android Gradle Plugin 8.9 and SDK 34.
