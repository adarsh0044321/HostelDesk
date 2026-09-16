package com.adarshsingh.adminhosteldesk.data.local;

import android.content.Context;
import android.content.SharedPreferences;
import com.adarshsingh.adminhosteldesk.BuildConfig;
import com.adarshsingh.adminhosteldesk.data.model.UserDto;

public class SessionManager {
    private static final String PREF_NAME = "HostelDeskAdminSession";
    private static final String KEY_TOKEN = "jwt_token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_NAME = "full_name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_ROLE = "role";
    private static final String KEY_DEPT_ID = "department_id";
    private static final String KEY_DEPT_NAME = "department_name";
    private static final String KEY_HOSTEL_NAME = "hostel_name";
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
            if (user.getPhone() != null) editor.putString(KEY_PHONE, user.getPhone());
            editor.putString(KEY_ROLE, user.getRole());
            if (user.getDepartmentId() != null) editor.putLong(KEY_DEPT_ID, user.getDepartmentId());
            editor.putString(KEY_DEPT_NAME, user.getDepartmentName());
            editor.putString(KEY_HOSTEL_NAME, user.getHostelName());
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

    public long getUserId() {
        return prefs.getLong(KEY_USER_ID, -1L);
    }

    public String getFullName() {
        return prefs.getString(KEY_NAME, "Operations Staff");
    }

    public String getEmail() {
        return prefs.getString(KEY_EMAIL, "");
    }

    public void setEmail(String email) {
        prefs.edit().putString(KEY_EMAIL, email).apply();
    }

    public String getPhone() {
        return prefs.getString(KEY_PHONE, "");
    }

    public void setPhone(String phone) {
        prefs.edit().putString(KEY_PHONE, phone).apply();
    }

    public String getRole() {
        return prefs.getString(KEY_ROLE, "WARDEN");
    }

    public String getDepartmentName() {
        return prefs.getString(KEY_DEPT_NAME, "");
    }

    public String getHostelName() {
        return prefs.getString(KEY_HOSTEL_NAME, "");
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


    public UserDto getUser() {
        if (!isLoggedIn()) return null;
        UserDto u = new UserDto();
        u.setId(getUserId());
        u.setFullName(getFullName());
        u.setEmail(getEmail());
        u.setPhone(getPhone());
        u.setRole(getRole());
        u.setDepartmentName(getDepartmentName());
        u.setHostelName(getHostelName());
        u.setInstituteCode(getInstituteCode());
        u.setInstituteName(getInstituteName());
        return u;
    }

    public void clearSession() {
        prefs.edit().clear().apply();
    }
}
