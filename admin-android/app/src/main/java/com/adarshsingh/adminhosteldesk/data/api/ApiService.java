package com.adarshsingh.adminhosteldesk.data.api;

import com.adarshsingh.adminhosteldesk.data.model.*;
import java.util.List;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {

    // Auth
    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @GET("auth/institutes/{code}")
    Call<InstitutePublicDto> getInstituteByCode(@Path("code") String code);

    @POST("auth/register-institute")
    Call<LoginResponse> registerInstitute(@Body RegisterInstituteRequest request);

    @POST("auth/forgot-password")
    Call<java.util.Map<String, String>> forgotPassword(@Body ForgotPasswordRequest request);

    @POST("auth/change-password")
    Call<java.util.Map<String, String>> changePassword(@Body ChangePasswordRequest request);

    @GET("auth/me")
    Call<UserDto> getMe();


    // Warden Dashboard & Admin APIs
    @GET("admin/dashboard")
    Call<WardenDashboardDto> getWardenDashboard();

    @GET("admin/issues")
    Call<List<IssueDto>> getAdminIssues(
            @Query("status") String status,
            @Query("priority") String priority,
            @Query("departmentId") Long departmentId
    );

    @GET("admin/issues/{id}")
    Call<IssueDetailDto> getIssueDetail(@Path("id") Long id);

    @POST("admin/issues/{id}/assign")
    Call<IssueDetailDto> assignIssue(
            @Path("id") Long id,
            @Body AssignIssueRequest request
    );

    @GET("admin/staff")
    Call<List<UserDto>> getStaffList();

    // Staff Technician APIs
    @GET("staff/issues")
    Call<List<IssueDto>> getStaffIssues(@Query("filter") String filter);

    @POST("staff/issues/{id}/start")
    Call<IssueDetailDto> startWork(@Path("id") Long id);

    @POST("staff/issues/{id}/progress-note")
    Call<IssueDetailDto> updateProgressNote(
            @Path("id") Long id,
            @Body UpdateProgressRequest request
    );

    @Multipart
    @POST("staff/issues/{id}/complete")
    Call<IssueDetailDto> completeWork(
            @Path("id") Long id,
            @Part("technicianNote") RequestBody technicianNote,
            @Part MultipartBody.Part proofPhoto
    );

    // AI Insights & Clustering
    @GET("analytics/insights")
    Call<List<InsightDto>> getInsights();

    @POST("analytics/detect-recurring")
    Call<List<InsightDto>> triggerRecurringDetection();

    // Institute Admin Onboarding & Management APIs
    @POST("institute/students")
    Call<CredentialResponse> createStudent(@Body CreateUserWithTempPasswordRequest request);

    @POST("institute/wardens")
    Call<CredentialResponse> createWarden(@Body CreateUserWithTempPasswordRequest request);

    @POST("institute/staff")
    Call<CredentialResponse> createStaff(@Body CreateUserWithTempPasswordRequest request);

    @GET("institute/hostels")
    Call<List<HostelDto>> getHostels();

    @POST("institute/hostels")
    Call<HostelDto> createHostel(@Body CreateHostelRequest request);

    @PUT("institute/hostels/{id}/assign-warden")
    Call<HostelDto> assignWarden(
            @Path("id") Long id,
            @Body AssignWardenRequest request
    );

    @PUT("institute/hostels/{id}")
    Call<HostelDto> updateHostel(
            @Path("id") Long id,
            @Body CreateHostelRequest request
    );

    @DELETE("institute/hostels/{id}")
    Call<java.util.Map<String, Object>> deleteHostel(
            @Path("id") Long id
    );

    @PUT("institute/wardens/my-contact")
    Call<UserDto> updateWardenContact(
            @Body UpdateContactRequest request
    );

    @GET("institute/crews")
    Call<List<CrewWorkloadDto>> getCrewWorkloads();

    @GET("institute/students")
    Call<List<UserDto>> getInstituteStudents();

    @GET("institute/wardens")
    Call<List<UserDto>> getInstituteWardens();

    @GET("institute/staff")
    Call<List<UserDto>> getInstituteStaff();

    // Institute Password Resets & Account Recovery (IT Desk)
    @GET("institute/password-resets")
    Call<List<PasswordResetDto>> getPasswordResets();

    @POST("institute/password-resets/{id}/assign")
    Call<PasswordResetDto> assignPasswordReset(
            @Path("id") Long id,
            @Body java.util.Map<String, String> body
    );

    @POST("institute/password-resets/{id}/approve")
    Call<CredentialResponse> approvePasswordReset(@Path("id") Long id);

    @POST("institute/password-resets/{id}/reject")
    Call<java.util.Map<String, String>> rejectPasswordReset(@Path("id") Long id);

    // Departments
    @GET("admin/departments")
    Call<List<DepartmentDto>> getDepartments();

    // Emergency Contacts
    @GET("institute/emergency-contacts")
    Call<EmergencyContactsDto> getEmergencyContacts();

    @PUT("institute/emergency-contacts")
    Call<EmergencyContactsDto> updateEmergencyContacts(@Body EmergencyContactsDto dto);

    // Announcements
    @POST("institute/announcements")
    Call<AnnouncementDto> createInstituteAnnouncement(@Body CreateAnnouncementRequest request);

    @POST("admin/announcements")
    Call<AnnouncementDto> createWardenAnnouncement(@Body CreateAnnouncementRequest request);

    @GET("admin/announcements")
    Call<List<AnnouncementDto>> getAnnouncements();

    @GET("institute/announcements")
    Call<List<AnnouncementDto>> getInstituteAnnouncements();

    @PUT("admin/announcements/{id}")
    Call<AnnouncementDto> updateAnnouncement(
            @Path("id") Long id,
            @Body CreateAnnouncementRequest request
    );

    @DELETE("admin/announcements/{id}")
    Call<java.util.Map<String, String>> deleteAnnouncement(
            @Path("id") Long id
    );
}
