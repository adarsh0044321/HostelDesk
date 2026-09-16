# HostelDesk Architectural & Design Decisions (ADR)

## ADR 001: Separation of Student and Admin Android Applications
- **Context**: College hostels require separate operational workflows. Students report and verify issues; Wardens and Maintenance crews triage, assign, update SLA tickets, and upload repair proof.
- **Decision**: Produce two independent Android APKs (`student-android` and `admin-android`).
- **Rationale**: Prevents accidental exposure of administrative APIs or sensitive hostel logs in the resident app, improves APK size, and enforces security boundaries.

## ADR 002: Java as the Core Language for Native Android & Spring Boot
- **Context**: The project requirement explicitly requests Java for both Android clients and the Spring Boot backend.
- **Decision**: Implement all Android activities, viewmodels, models, and repositories in Java 17, and backend in Spring Boot 3 with Java 17.
- **Rationale**: Satisfies the explicit requirement, ensuring strong type safety, robust backward compatibility, and clean collegiate software engineering pedagogy.

## ADR 003: Deterministic Rule Fallback for Python AI Service
- **Context**: Network partitions, AI downtime, or inference crashes must not block a student from submitting an emergency pipe burst or electrical hazard.
- **Decision**: The Spring Boot backend connects to Python FastAPI with a 3-second timeout and circuit breaker. If the AI service is unreachable, a deterministic Java rule classifier predicts category and priority, setting `isFallback = true`.
- **Rationale**: High operational availability and resilience.

## ADR 004: Dual-Profile Persistence Architecture
- **Context**: The backend targets PostgreSQL 14+ with Flyway migrations. To ensure developer velocity and deterministic test execution in offline or CI environments, a fallback profile is supported.
- **Decision**: Configure `application.yml` for PostgreSQL as the default profile, with an active/test profile utilizing H2 in PostgreSQL compatibility mode.
- **Rationale**: Maximum portability and zero-downtime automated test verification.

## ADR 005: Deployment of Spring Boot to Cloud PaaS (Render) & Supabase Hosted PostgreSQL
- **Context**: Campus Wi-Fi networks block outbound raw database ports (`5432`, `6543`), preventing direct connections from student mobile devices or workstations. Additionally, keeping the developer workstation powered on 24/7 is not feasible.
- **Decision**: Containerize Spring Boot 3 with Docker and deploy to Render.com connected to a Supabase Cloud PostgreSQL pooler over internal cloud backbones.
- **Rationale**: Android devices connect to Render over standard HTTPS port `443` (unblocked on all networks), and the backend runs 24/7 with zero PC dependency.

## ADR 006: Dynamic Session Host Migration & Flexible DTO Field Aliasing
- **Context**: Devices with existing app installs had cached local IP addresses (`10.0.2.2`, `10.110.0.170`), and variations in Android payload naming caused HTTP 400 validation failures on login.
- **Decision**: Equip `SessionManager` with auto-migration to the Render Cloud endpoint, and decorate backend `LoginRequest` with Jackson `@JsonAlias({"email", "username", "institutionalId"})`.
- **Rationale**: Guarantees backwards compatibility, prevents login failures, and delivers seamless one-click onboarding across all test devices.

## ADR 007: Shared-Schema Multi-Tenancy with Explicit Institute Discriminator
- **Context**: HostelDesk needs to support multiple independent educational institutions (`Institute` -> `Campus` -> `Hostel` -> `Block` -> `Floor` -> `Room` -> `Student`), preventing cross-tenant data leaks while keeping operations simple on PostgreSQL.
- **Decision**: Implement shared-schema multi-tenancy via `institutes` table and `institute_id` foreign keys on core domain entities (`users`, `hostels`, `departments`, `issues`, `routing_rules`, `password_reset_requests`) via Flyway migration `V4__multi_tenant_institutes.sql`. Enforce isolation server-side in all repository queries and embed `instituteId` / `instituteCode` into JWT claims.
- **Rationale**: Provides strict data security boundaries with zero cross-tenant contamination, straightforward backups, and efficient connection pooling.

## ADR 008: JetBrains Runtime C2 JIT Compiler Bug Mitigation (`-XX:TieredStopAtLevel=1`)
- **Context**: On developer workstations running JetBrains Runtime (JBR-17), running Maven Surefire unit tests triggered an internal JVM crash (`Internal Error (opto/node.hpp:1125), guarantee(t != nullptr) failed: must be con`) during ByteBuddy dynamic proxy compilation.
- **Decision**: Add `<argLine>-XX:TieredStopAtLevel=1</argLine>` to `maven-surefire-plugin` in `pom.xml`.
- **Rationale**: Restricts the JVM to C1 (client) compilation during test execution, completely preventing the C2 compiler crash while maintaining fast test execution.

## ADR 009: Client-Side Android Image Bounds Downsampling (1920px Max) for OOM Prevention
- **Context**: Modern smartphone cameras take 12MP-108MP photos (4000x3000 to 12000x9000). Decoding these raw images directly onto the Android UI heap causes `OutOfMemoryError` or severe UI freezing during issue submission.
- **Decision**: Perform two-pass decoding in a background executor thread: first decode with `inJustDecodeBounds = true` to measure dimensions, calculate `inSampleSize` power-of-two reduction bounded to a max dimension of 1920px, decode the scaled bitmap, and compress to JPEG at 85% quality.
- **Rationale**: Keeps heap memory usage low (<15MB), ensures fast upload speed over campus Wi-Fi, preserves fine detail for maintenance diagnosis, and prevents UI thread stalls.

## ADR 010: JPA Lazy Entity Protection against Jackson ByteBuddy Serialization
- **Context**: Serializing entities with `@ManyToOne(fetch = FetchType.LAZY)` (such as `PasswordResetRequest.user`) directly to JSON causes Jackson to attempt serializing Hibernate's `ByteBuddyInterceptor`, resulting in `HttpMessageConversionException`.
- **Decision**: Annotate internal lazy entity fields with `@JsonIgnore` and expose clean, explicit non-lazy getter primitives (`getUserId`, `getUserFullName`, `getUserEmail`, `getInstituteCode`) or dedicated DTOs.
- **Rationale**: Guarantees zero runtime serialization crashes and prevents accidental lazy loading queries or N+1 leaks outside transaction boundaries.

## ADR 011: Public Unauthenticated Dynamic Institute Verification
- **Context**: Users typing an Institute ID/Code previously saw static hardcoded strings ("Tagore & Shastri"), or were forced to submit full login credentials before knowing if the institute was valid.
- **Decision**: Expose `GET /api/auth/institutes/{code}` returning public metadata (`InstitutePublicDto`: code, name, campusName, activeHostelsCount, helpline). Android clients use a 500ms debounced `TextWatcher` to dynamically fetch and display the institute title, campus name, and verification status badge.
- **Rationale**: Elevates UX confidence, eliminates hardcoded campus names, and prevents invalid login submissions.

## ADR 012: Permanent Cloud Base URL Locking & Removal of Server Switcher Dialogs
- **Context**: The server switcher dialog was a temporary developer bridge during local IP and emulator testing. In production, users should not be confused by local IP options (`10.0.2.2`, `10.110.0.170`).
- **Decision**: Hardcode the base API URL to Render Cloud (`https://hosteldesk-backend-pc8z.onrender.com/api/`) via `BuildConfig.BASE_URL`, purge server switcher dialogs, and eliminate fake network inspection banners.
- **Rationale**: Simplifies onboarding, avoids user confusion, and guarantees uniform cloud backend connectivity over port 443 HTTPS.


