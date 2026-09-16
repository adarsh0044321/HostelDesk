# HostelDesk Security Specification & Architecture

## 1. Password Security
- Passwords are encrypted using **BCrypt** with a salt round factor of 12.
- Neither plaintext passwords nor password hashes are ever returned in API response bodies.
- Passwords are strictly validated for minimal complexity on registration/reset.

## 2. Token Authentication & Stateless Authorization
- System uses JSON Web Tokens (JWT) signed with HMAC-SHA256.
- The token payload carries the subject (User ID), email, institutional ID, and granted role authorities (`ROLE_STUDENT`, `ROLE_WARDEN`, etc.).
- Token expiration is set to 24 hours. Expired tokens yield HTTP 401 Unauthorized, prompting the Android client to safely redirect to Login without crash.

## 3. Role-Based Access Control (RBAC)
- Spring Security enforces authorization at both the filter chain and method level (`@PreAuthorize`).
- Endpoint security rules:
  - `/api/student/**`: strictly requires `hasRole('STUDENT')`.
  - `/api/admin/**`: requires `hasAnyRole('WARDEN', 'ADMIN')`.
  - `/api/staff/**`: requires `hasRole('MAINTENANCE_STAFF')`.
  - `/api/auth/**`: public endpoints for login and token refresh.
- Role boundary protection: The login controller explicitly evaluates the target application (`targetApp`). A student credential submitted to the Admin Android client is rejected with HTTP 403 Forbidden.

## 4. File Upload & Storage Hardening
- File uploads are validated before processing:
  - MIME types whitelist: `image/jpeg`, `image/png`, `image/webp`.
  - Maximum upload size: 10 MB per file.
  - Client-supplied filenames are never used directly on the filesystem. Filenames are regenerated using `UUID.randomUUID()` with sanitized extensions.
  - Uploaded files are stored outside the web root in a designated `./uploads` directory.

## 5. Defense in Depth & Error Handling
- Global exception handler catches all exceptions and transforms them into standardized JSON error responses (`timestamp`, `status`, `error`, `message`, `path`).
- Java stack traces, SQL syntax snippets, and internal database exception details are completely suppressed from client responses.
- CORS is strictly configured to allow only authorized clients.
