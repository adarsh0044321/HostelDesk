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
- **Java**: JDK 17 LTS (OpenJDK, Eclipse Temurin, or Oracle JDK)
- **Maven**: 3.8+
- **Gradle**: 8.5+ (or bundled Gradle wrapper `./gradlew`)
- **Android SDK**: API 34+ (Android Studio Koala / Jellyfish recommended)
- **Python**: 3.11+ (with pip and virtualenv)
- **PostgreSQL**: PostgreSQL 14+ or cloud instance (or embedded H2 mode for offline testing)

---

### 1. Running the Python AI Service (Local)
```bash
cd ai-service
python -m venv venv

# Windows:
.\venv\Scripts\activate
# Linux / macOS:
source venv/bin/activate

pip install -r requirements.txt
python -m uvicorn main:app --host 0.0.0.0 --port 8000 --reload
```
Verify via browser: `http://localhost:8000/health` or `http://localhost:8000/docs`.

---

### 2. Running the Java Spring Boot Backend (Local)
```bash
cd backend

# Option A: Run with local PostgreSQL profile (configure .env or application.yml)
mvn spring-boot:run -Dspring-boot.run.profiles=postgres

# Option B: Run with embedded H2 PostgreSQL-compatible mode (zero setup needed)
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```
Backend starts on port 8080. Health endpoint: `http://localhost:8080/actuator/health`.

---

### 3. Building the Android Applications from Source

Ensure `JAVA_HOME` points to JDK 17 and `ANDROID_HOME` points to your Android SDK.

#### Build Student Android App
```bash
cd student-android

# Windows:
gradlew.bat assembleDebug
# Linux / macOS:
./gradlew assembleDebug
```
Output APK: `student-android/app/build/outputs/apk/debug/app-debug.apk`

#### Build Admin/Staff Android App
```bash
cd admin-android

# Windows:
gradlew.bat assembleDebug
# Linux / macOS:
./gradlew assembleDebug
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
