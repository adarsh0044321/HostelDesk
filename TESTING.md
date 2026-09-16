# HostelDesk Testing Protocol & Verification Strategy

## 1. Automated Test Levels
1. **Backend Unit & Integration Tests (Spring Boot)**:
   - Security: Password hashing with BCrypt, JWT generation/validation, role restriction enforcement.
   - Domain: Legal state machine transitions vs. illegal transitions (`InvalidStateTransitionException`).
   - Routing Engine: Category to department mapping.
   - Controllers: MockMvc tests verifying HTTP response codes (200, 201, 401, 403, 404, 422).
   - AI Fallback: Simulation of Python AI downtime and verified fallback tagging.

2. **Python AI Pytest Suite**:
   - Classification accuracy across plumbing, electrical, carpentry, cleaning, IT.
   - Priority rating (P1 Urgent vs. P3 Medium).
   - Cluster detection across recurring complaints in the same block/room group.

3. **Android Build & Lint Verification**:
   - `gradle assembleDebug` for both `student-android` and `admin-android`.
   - Bytecode verification, resource resolution, and APK output.

## 2. End-to-End Verification Scenario
1. Start AI service: `python -m uvicorn main:app --port 8000`.
2. Start Backend: `mvn spring-boot:run` on port 8080.
3. Authenticate as Aarav (`aarav@campus.edu`), verify student dashboard metrics.
4. Report an issue: "Bathroom ceiling water leakage near light" with image.
5. Confirm AI categorizes as `PLUMBING`, priority `P1_URGENT`.
6. Confirm Warden Sharma (`warden.sharma@campus.edu`) views ticket in "Attention required" SLA queue.
7. Warden assigns ticket to Suresh Kumar (`suresh@campus.edu`).
8. Suresh Kumar starts work, logs note: "Replaced faulty seal", uploads proof photo.
9. Aarav receives notification: "Issue HD-1042 is ready for verification".
10. Aarav clicks "Yes, it is fixed". Database status becomes `RESOLVED`.
11. Test Reopen scenario with second ticket: Click "No, still leaking", status becomes `REOPENED`.
