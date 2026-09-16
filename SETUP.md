# HostelDesk Setup & Execution Guide

## Quick Start (Cloud Production — No PC Required)

The backend and database are deployed 24/7 in the cloud:
- **Cloud Backend**: `https://hosteldesk-backend-pc8z.onrender.com`
- **Cloud Health Check**: `https://hosteldesk-backend-pc8z.onrender.com/actuator/health`
- **Cloud Database**: Hosted PostgreSQL on Supabase (`aws-0-ap-northeast-2.pooler.supabase.com:5432`)

### 1. Install Pre-built APKs on Phone
Both native APKs are pre-compiled and configured to connect directly to the Cloud Backend over HTTPS (Port 443).

**Option A: One-Click USB Install (via ADB)**
Connect your Android device via USB with USB Debugging enabled, then run:
```powershell
.\install-apks.bat
```

**Option B: Direct File Transfer**
Copy the following APKs to your phone and install:
- Student App: [`apks/hosteldesk-student-debug.apk`](file:///c:/Users/JAISINGH/OneDrive/Documents/antigravity/studenttdesk/apks/hosteldesk-student-debug.apk) (7.0 MB)
- Admin/Staff App: [`apks/hosteldesk-admin-debug.apk`](file:///c:/Users/JAISINGH/OneDrive/Documents/antigravity/studenttdesk/apks/hosteldesk-admin-debug.apk) (6.9 MB)

> [!NOTE]
> `SessionManager.java` automatically detects and upgrades any stale localhost/emulator IPs (`10.0.2.2`, `127.0.0.1`, `10.110.0.170`) to the cloud production Render URL. You can also tap **Server Settings** on the login screen to switch servers at any time.

---

## Local Development Setup (Optional)

### Prerequisites
- **Java**: JDK 17 (verified with JetBrains JBR 17.0.12 or Oracle JDK 17+)
- **Maven**: 3.9+ (or use IntelliJ bundled Maven at `C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2023.3.8\plugins\maven\lib\maven3\bin\mvn.cmd`)
- **Gradle**: 8.5+ (or Gradle 8.9 wrapper at `C:\Users\JAISINGH\.gradle\wrapper\dists\gradle-8.9-bin\90cnw93cvbtalezasaz0blq0a\gradle-8.9\bin\gradle.bat`)
- **Android SDK**: API 34+ installed at `C:\Users\JAISINGH\AppData\Local\Android\Sdk`
- **Python**: 3.11+ (FastAPI, Uvicorn, Pydantic)
- **PostgreSQL**: PostgreSQL 14+ or Supabase Cloud DB (or automated H2 PostgreSQL-compatible fallback for offline/development test suites)

---

### 1. Running the Python AI Service (Local)
```powershell
cd c:\Users\JAISINGH\OneDrive\Documents\antigravity\studenttdesk\ai-service
python -m uvicorn main:app --host 0.0.0.0 --port 8000 --reload
```
Verify via browser/curl: `http://localhost:8000/health` or `http://localhost:8000/docs`.

---

### 2. Running the Java Spring Boot Backend (Local)
```powershell
cd c:\Users\JAISINGH\OneDrive\Documents\antigravity\studenttdesk\backend
$env:JAVA_HOME = "C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2023.3.8\jbr"
& "C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2023.3.8\plugins\maven\lib\maven3\bin\mvn.cmd" spring-boot:run
```
Backend starts on port 8080. Check health at: `http://localhost:8080/actuator/health`.

---

### 3. Building the Android Applications from Source
Set Java Home and Android Home:
```powershell
$env:JAVA_HOME = "C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2023.3.8\jbr"
$env:ANDROID_HOME = "C:\Users\JAISINGH\AppData\Local\Android\Sdk"
```

#### Build Student Android App
```powershell
cd c:\Users\JAISINGH\OneDrive\Documents\antigravity\studenttdesk\student-android
& "C:\Users\JAISINGH\.gradle\wrapper\dists\gradle-8.9-bin\90cnw93cvbtalezasaz0blq0a\gradle-8.9\bin\gradle.bat" assembleDebug
```
Output APK: `student-android/app/build/outputs/apk/debug/app-debug.apk`

#### Build Admin/Staff Android App
```powershell
cd c:\Users\JAISINGH\OneDrive\Documents\antigravity\studenttdesk\admin-android
& "C:\Users\JAISINGH\.gradle\wrapper\dists\gradle-8.9-bin\90cnw93cvbtalezasaz0blq0a\gradle-8.9\bin\gradle.bat" assembleDebug
```
Output APK: `admin-android/app/build/outputs/apk/debug/app-debug.apk`

---

## 4. Default Seed Credentials

| Role | Email | Password | Scope |
| :--- | :--- | :--- | :--- |
| Student | `aarav@campus.edu` | `student123` | Tagore Hall, Block B, Room 204 |
| Warden | `warden.sharma@campus.edu` | `warden123` | North Campus |
| Maintenance Staff | `suresh@campus.edu` | `staff123` | Block B Plumbing |
| Administrator | `admin@campus.edu` | `admin123` | Global Administration |
