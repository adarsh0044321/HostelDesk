# HostelDesk Engineering Handover

## Project Overview
HostelDesk is a comprehensive, production-grade collegiate hostel issue management platform. It features two native Java Android applications (Student and Admin/Staff), a Java Spring Boot 3 backend deployed to Render.com, a hosted PostgreSQL database on Supabase, and a Python FastAPI AI service with deterministic fallback.

## Live Cloud Infrastructure (24/7 Availability)
- **Cloud Backend API**: `https://hosteldesk-backend-pc8z.onrender.com`
- **Actuator Health Endpoint**: `https://hosteldesk-backend-pc8z.onrender.com/actuator/health`
- **Cloud Database**: Supabase PostgreSQL (`aws-0-ap-northeast-2.pooler.supabase.com:5432`)
- **Backend GitHub Repo**: `https://github.com/adarsh0044321/hosteldesk-backend`

## Pre-compiled Mobile Deliverables
Pre-built, ready-to-run native Android APKs configured for the cloud backend:
- **Student App**: [`apks/hosteldesk-student-debug.apk`](file:///c:/Users/JAISINGH/OneDrive/Documents/antigravity/studenttdesk/apks/hosteldesk-student-debug.apk) (7.0 MB)
- **Admin/Staff App**: [`apks/hosteldesk-admin-debug.apk`](file:///c:/Users/JAISINGH/OneDrive/Documents/antigravity/studenttdesk/apks/hosteldesk-admin-debug.apk) (6.9 MB)
- **One-Click USB Installer**: [`install-apks.bat`](file:///c:/Users/JAISINGH/OneDrive/Documents/antigravity/studenttdesk/install-apks.bat)

## Repository Layout
- `student-android/`: Native Android application for university residents (`com.adarshsingh.hosteldesk`).
- `admin-android/`: Native Android application for Wardens and Maintenance Technicians (`com.adarshsingh.adminhosteldesk`).
- `backend/`: Java Spring Boot 3 modular monolith.
- `ai-service/`: Python 3.11 FastAPI NLP classification and clustering service.
- `database/`: Reference SQL DDL and seed scripts (`schema.sql`, `seed.sql`).
- `apks/`: Direct distribution binaries for mobile testing.
- Documentation: `README.md`, `ARCHITECTURE.md`, `DATABASE.md`, `API.md`, `SECURITY.md`, `SETUP.md`, `TESTING.md`, `DECISIONS.md`, `BRAIN.md`, `ROADMAP.md`.

## Multi-Tenant Institutes & Authentication
HostelDesk isolates student, hostel, staff, and issue data by educational institution:
- **Default Institute**: `NCH-001` (*North Campus Hostels · Main Campus*)
- **Secondary Tenant (Isolation Verification)**: `SCH-002` (*South Campus Hostels*)
- **Self-Registration**: New institutions can register directly via the Admin App using "New Institution? Register Campus" (calls `POST /api/auth/register-institute`).

Both native Android applications (`student-android` and `admin-android`) dynamically resolve the **Institute ID / Code** in real-time as the user types, fetching and displaying the campus name, active hostel count, and official emergency helpline.

## Seed Accounts & Roles (Institute: `NCH-001`)
| Role | Email / Username | Password | Scope / Department |
| :--- | :--- | :--- | :--- |
| **Student** | `aarav@campus.edu` | `student123` | Tagore Hall, Block B, Room 204 |
| **Warden** | `warden.sharma@campus.edu` | `warden123` | North Campus Operations Desk |
| **Maintenance Staff** | `suresh@campus.edu` | `staff123` | Block B Plumbing Team |
| **Institute Admin** | `admin@campus.edu` | `admin123` | North Campus Hostels Facility Admin |

*(For South Campus Hostels `SCH-002`, use `priya@southcampus.edu` / `student123`)*


## Institutional Password Reset Operations
1. **Student / Staff Request**:
   - In either Android app, tap "Forgot Password?" or "Reset Access Code".
   - Enter your Institute Code (`NCH-001`), registered Email/ID, and optional contact phone.
   - Or call API: `POST /api/auth/forgot-password`
2. **Administrator Approval**:
   - Institute Admin logs in with `admin@campus.edu`.
   - Inspect queue: `GET /api/institute/password-resets`
   - Approve and assign temp password: `POST /api/institute/password-resets/{id}/approve` with `{"temporaryPassword": "newpassword123"}`.

## Running Locally vs Cloud
- **Local Dev Server**:
  ```powershell
  cd backend
  powershell -ExecutionPolicy Bypass -File .\run-backend.ps1
  ```
  Runs Spring Boot with the `dev` profile on port 8080 with embedded H2 (PostgreSQL mode) and auto-seeded multi-tenant data.
- **Run Maven Test Suite**:
  ```powershell
  mvn clean test
  ```
  Executes all 14 unit and integration tests including `MultiTenantIsolationTest`.

