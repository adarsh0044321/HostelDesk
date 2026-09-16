# HostelDesk REST API Specification

Base URL (Cloud Production): `https://hosteldesk-backend-pc8z.onrender.com/api`  
Base URL (Local Fallback): `http://localhost:8080/api`

## Authentication & Authorization
All secured endpoints expect the header:
`Authorization: Bearer <JWT_TOKEN>`

---

## 1. Auth Controller (`/api/auth`)
- `POST /api/auth/login`
  - Body: `{ "email": "aarav@campus.edu", "password": "student123", "targetApp": "STUDENT" | "ADMIN" }`
  - *Note: Accepts `email`, `username`, or `emailOrInstitutionalId` interchangeably via `@JsonAlias`.*
  - Response: `{ "token": "...", "tokenType": "Bearer", "expiresIn": 86400, "user": { ... } }`
  - *Note: Login validates that `STUDENT` role cannot log into Admin App.*
- `GET /api/auth/me`
  - Response: Current user profile object.
- `POST /api/auth/forgot-password`
  - Body: `{ "email": "aarav@campus.edu" }`
  - Response: `{ "message": "Password reset instructions sent." }`

---

## 2. Student Endpoints (`/api/student`)
- `GET /api/student/dashboard`
  - Role: `STUDENT`
  - Returns vitals (water, power status), active issue count, recent issues, announcements.
- `POST /api/student/issues`
  - Role: `STUDENT`
  - Content-Type: `multipart/form-data`
  - Form fields: `title`, `description`, `category`, `priority`, `blockName`, `roomNumber`, `attachment` (file)
  - Triggers synchronous/asynchronous AI analysis and automatic department routing.
- `GET /api/student/issues`
  - Role: `STUDENT`
  - Query params: `status`, `page`, `size`
  - Returns resident's tickets.
- `GET /api/student/issues/{id}`
  - Role: `STUDENT`
  - Returns issue detail, timeline activities, attachments, and AI summary.
- `POST /api/student/issues/{id}/verify`
  - Role: `STUDENT`
  - Body: `{ "satisfactionNote": "Tap is fixed and dry" }`
  - Transitions status from `AWAITING_VERIFICATION` to `RESOLVED`.
- `POST /api/student/issues/{id}/reopen`
  - Role: `STUDENT`
  - Body: `{ "reason": "Water is still leaking after 10 mins" }`
  - Transitions status from `AWAITING_VERIFICATION` to `REOPENED`.
- `GET /api/student/notifications`
  - Role: `STUDENT`
  - Returns notification alerts.
- `PUT /api/student/notifications/{id}/read`
  - Marks notification as read.

---

## 3. Admin & Warden Endpoints (`/api/admin`)
- `GET /api/admin/dashboard`
  - Role: `WARDEN`, `ADMIN`
  - Returns operational overview: Total open, Urgent (P1), In work, Pending check, Residence health pulse, Attention required queue.
- `GET /api/admin/issues`
  - Role: `WARDEN`, `ADMIN`
  - Query params: `department`, `priority`, `status`, `search`
- `POST /api/admin/issues/{id}/assign`
  - Role: `WARDEN`, `ADMIN`
  - Body: `{ "departmentId": 1, "staffId": 3, "notes": "Inspect today" }`
  - Transitions to `ASSIGNED` and sends staff notification.
- `POST /api/admin/issues/{id}/escalate`
  - Role: `WARDEN`, `ADMIN`
  - Notifies department lead and bumps priority to P1.
- `GET /api/admin/insights/recurring`
  - Returns AI-identified recurring issue clusters and recommendations.

---

## 4. Maintenance Staff Endpoints (`/api/staff`)
- `GET /api/staff/issues`
  - Role: `MAINTENANCE_STAFF`
  - Query params: `filter` (`MY_WORK`, `QUEUE`, `COMPLETED`)
- `POST /api/staff/issues/{id}/start`
  - Role: `MAINTENANCE_STAFF`
  - Transitions issue to `IN_PROGRESS`.
- `POST /api/staff/issues/{id}/progress-note`
  - Role: `MAINTENANCE_STAFF`
  - Body: `{ "note": "Valve replaced; observing pressure" }`
- `POST /api/staff/issues/{id}/complete`
  - Role: `MAINTENANCE_STAFF`
  - Content-Type: `multipart/form-data`
  - Form fields: `technicianNote`, `proofPhoto` (file)
  - Transitions issue to `AWAITING_VERIFICATION` and triggers student verification alert.

---

## 5. File Controller (`/api/files`)
- `GET /api/files/{filename}`
  - Streams uploaded images/attachments with proper Content-Type headers.
