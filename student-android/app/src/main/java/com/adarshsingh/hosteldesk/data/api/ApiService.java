package com.adarshsingh.hosteldesk.data.api;

import com.adarshsingh.hosteldesk.data.model.*;
import java.util.List;
import java.util.Map;
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

    @POST("auth/forgot-password")
    Call<Map<String, String>> forgotPassword(@Body ForgotPasswordRequest request);

    @POST("auth/change-password")
    Call<Map<String, String>> changePassword(@Body ChangePasswordRequest request);

    @PUT("auth/profile")
    Call<UserDto> updateProfile(@Body UpdateProfileRequest request);

    @GET("auth/me")
    Call<UserDto> getMe();


    // Student Dashboard & Issues
    @GET("student/dashboard")
    Call<StudentDashboardDto> getDashboard();

    @GET("student/issues")
    Call<List<IssueDto>> getMyIssues(@Query("status") String status);

    @GET("student/issues/{id}")
    Call<IssueDetailDto> getIssueDetail(@Path("id") Long id);

    @Multipart
    @POST("student/issues")
    Call<IssueDetailDto> createIssue(
            @Part("title") RequestBody title,
            @Part("description") RequestBody description,
            @Part("category") RequestBody category,
            @Part("blockName") RequestBody blockName,
            @Part("roomNumber") RequestBody roomNumber,
            @Part("priority") RequestBody priority,
            @Part MultipartBody.Part attachment
    );

    @POST("student/issues/{id}/verify")
    Call<IssueDetailDto> verifyResolution(
            @Path("id") Long id,
            @Body VerifyResolutionRequest request
    );

    @POST("student/issues/{id}/reopen")
    Call<IssueDetailDto> reopenIssue(
            @Path("id") Long id,
            @Body ReopenIssueRequest request
    );

    // Notifications
    @GET("notifications")
    Call<List<NotificationDto>> getNotifications();

    @PUT("notifications/{id}/read")
    Call<Map<String, String>> markNotificationAsRead(@Path("id") Long id);

    @PUT("notifications/read-all")
    Call<Map<String, String>> markAllNotificationsAsRead();

    @GET("notifications/unread-count")
    Call<Map<String, Long>> getUnreadCount();

    // Emergency Contacts & Announcements
    @GET("student/emergency-contacts")
    Call<EmergencyContactsDto> getEmergencyContacts();

    @GET("student/announcements")
    Call<List<AnnouncementDto>> getAnnouncements();
}
