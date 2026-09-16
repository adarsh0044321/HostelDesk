package com.adarshsingh.hosteldesk.data.local;

import android.content.Context;
import android.content.SharedPreferences;
import com.adarshsingh.hosteldesk.BuildConfig;
import com.adarshsingh.hosteldesk.data.model.UserDto;

public class SessionManager {
    private static final String PREF_NAME = "HostelDeskStudentSession";
    private static final String KEY_TOKEN = "jwt_token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_NAME = "full_name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_STUDENT_ID = "institutional_id";
    private static final String KEY_HOSTEL_NAME = "hostel_name";
    private static final String KEY_ROOM_NUMBER = "room_number";
    private static final String KEY_ROLE = "role";
    private static final String KEY_BATCH = "batch";
    private static final String KEY_INSTITUTE_CODE = "institute_code";
    private static final String KEY_INSTITUTE_NAME = "institute_name";
    private static final String KEY_BASE_URL = "custom_base_url";

    private final SharedPreferences prefs;
    private static SessionManager instance;

    public SessionManager(Context context) {
        this.prefs = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context);
        }
        return instance;
    }

    public void saveSession(String token, UserDto user) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_TOKEN, token);
        if (user != null) {
            if (user.getId() != null) editor.putLong(KEY_USER_ID, user.getId());
            editor.putString(KEY_NAME, user.getFullName());
            editor.putString(KEY_EMAIL, user.getEmail());
            editor.putString(KEY_STUDENT_ID, user.getInstitutionalId());
            editor.putString(KEY_HOSTEL_NAME, user.getHostelName());
            editor.putString(KEY_ROOM_NUMBER, user.getRoomNumber());
            editor.putString(KEY_ROLE, user.getRole());
            if (user.getBatch() != null) editor.putString(KEY_BATCH, user.getBatch());
            if (user.getInstituteCode() != null) editor.putString(KEY_INSTITUTE_CODE, user.getInstituteCode());
            if (user.getInstituteName() != null) editor.putString(KEY_INSTITUTE_NAME, user.getInstituteName());
        }
        editor.apply();
    }

    public boolean isLoggedIn() {
        return getToken() != null && !getToken().isEmpty();
    }

    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

    public String getFullName() {
        return prefs.getString(KEY_NAME, "Resident Student");
    }

    public String getEmail() {
        return prefs.getString(KEY_EMAIL, "");
    }

    public String getStudentId() {
        return prefs.getString(KEY_STUDENT_ID, "");
    }

    public String getHostelName() {
        return prefs.getString(KEY_HOSTEL_NAME, "");
    }

    public String getRoomNumber() {
        return prefs.getString(KEY_ROOM_NUMBER, "");
    }

    public String getRole() {
        return prefs.getString(KEY_ROLE, "STUDENT");
    }

    public String getBatch() {
        return prefs.getString(KEY_BATCH, "");
    }

    public String getInstituteCode() {
        return prefs.getString(KEY_INSTITUTE_CODE, "NCH-001");
    }

    public String getInstituteName() {
        return prefs.getString(KEY_INSTITUTE_NAME, "");
    }

    public String getBaseUrl() {
        return BuildConfig.BASE_URL;
    }

    public void setCustomBaseUrl(String url) {
        // Render Cloud is default permanent backend
    }


    public void updateUserProfile(String fullName, String roomNumber) {
        SharedPreferences.Editor editor = prefs.edit();
        if (fullName != null) editor.putString(KEY_NAME, fullName);
        if (roomNumber != null) editor.putString(KEY_ROOM_NUMBER, roomNumber);
        editor.apply();
    }

    public UserDto getUser() {
        if (!isLoggedIn()) return null;
        UserDto u = new UserDto();
        u.setId(prefs.getLong(KEY_USER_ID, 0L));
        u.setFullName(getFullName());
        u.setEmail(getEmail());
        u.setInstitutionalId(getStudentId());
        u.setHostelName(getHostelName());
        u.setRoomNumber(getRoomNumber());
        u.setRole(getRole());
        u.setBatch(getBatch());
        u.setInstituteCode(getInstituteCode());
        u.setInstituteName(getInstituteName());
        return u;
    }

    public void clearSession() {
        prefs.edit().clear().apply();
    }
}
