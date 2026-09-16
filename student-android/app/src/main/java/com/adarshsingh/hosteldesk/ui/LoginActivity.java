package com.adarshsingh.hosteldesk.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import com.adarshsingh.hosteldesk.data.api.ApiClient;
import com.adarshsingh.hosteldesk.data.local.SessionManager;
import com.adarshsingh.hosteldesk.data.model.InstitutePublicDto;
import com.adarshsingh.hosteldesk.data.model.LoginRequest;
import com.adarshsingh.hosteldesk.data.model.LoginResponse;
import com.adarshsingh.hosteldesk.databinding.ActivityLoginBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {


    private ActivityLoginBinding binding;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = SessionManager.getInstance(this);

        setupInstituteLookup();

        binding.tvForgotPassword.setOnClickListener(v -> showForgotPasswordDialog());
        binding.btnRegisterInstitute.setOnClickListener(v -> showRegisterGuidanceDialog());
        binding.btnLogin.setOnClickListener(v -> attemptLogin());
    }


    private void showForgotPasswordDialog() {
        android.widget.LinearLayout container = new android.widget.LinearLayout(this);
        container.setOrientation(android.widget.LinearLayout.VERTICAL);
        int pad = (int) (16 * getResources().getDisplayMetrics().density);
        container.setPadding(pad, pad, pad, pad);

        android.widget.EditText etInst = new android.widget.EditText(this);
        etInst.setHint("Institute Code (e.g. NCH-001)");
        etInst.setText(binding.etInstituteCode.getText() != null ? binding.etInstituteCode.getText().toString().trim() : "NCH-001");
        container.addView(etInst);

        android.widget.EditText etIdent = new android.widget.EditText(this);
        etIdent.setHint("Student ID or Email (e.g. CS2026-001)");
        etIdent.setText(binding.etEmail.getText() != null ? binding.etEmail.getText().toString().trim() : "");
        container.addView(etIdent);

        android.widget.EditText etPhone = new android.widget.EditText(this);
        etPhone.setHint("Contact Phone Number (for IT Desk callback)");
        etPhone.setInputType(android.text.InputType.TYPE_CLASS_PHONE);
        container.addView(etPhone);

        android.widget.EditText etReason = new android.widget.EditText(this);
        etReason.setHint("Reason / Issue (e.g. Lost device, forgot password)");
        container.addView(etReason);

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Institute IT Account Recovery")
                .setMessage("Submit request directly to the Institute IT & Account Recovery Desk. Provide your contact number so an IT officer can verify your identity.")
                .setView(container)
                .setPositiveButton("Submit to IT Desk", (dialog, which) -> {
                    String inst = etInst.getText().toString().trim();
                    String ident = etIdent.getText().toString().trim();
                    String phone = etPhone.getText().toString().trim();
                    String reason = etReason.getText().toString().trim();

                    if (inst.isEmpty() || ident.isEmpty()) {
                        android.widget.Toast.makeText(this, "Institute ID and Student ID/Email are required", android.widget.Toast.LENGTH_SHORT).show();
                        return;
                    }

                    com.adarshsingh.hosteldesk.data.model.ForgotPasswordRequest req =
                            new com.adarshsingh.hosteldesk.data.model.ForgotPasswordRequest(inst, ident, "STUDENT", reason, phone);

                    ApiClient.getApiService(this).forgotPassword(req).enqueue(new Callback<java.util.Map<String, String>>() {
                        @Override
                        public void onResponse(Call<java.util.Map<String, String>> call, Response<java.util.Map<String, String>> response) {
                            if (response.isSuccessful()) {
                                String contactMsg = phone.isEmpty() ? "" : ("\n\nAn IT officer will contact you on " + phone + " to verify your identity.");
                                new androidx.appcompat.app.AlertDialog.Builder(LoginActivity.this)
                                        .setTitle("Request Routed to Institute IT")
                                        .setMessage("Your account recovery request has been routed to the Institute IT Helpdesk." + contactMsg + "\n\nOnce approved, you will be issued temporary credentials.")
                                        .setPositiveButton("OK", null)
                                        .show();
                            } else {
                                android.widget.Toast.makeText(LoginActivity.this, "Failed to submit request (" + response.code() + ")", android.widget.Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<java.util.Map<String, String>> call, Throwable t) {
                            android.widget.Toast.makeText(LoginActivity.this, "Error submitting request: " + t.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private final Handler lookupHandler = new Handler(Looper.getMainLooper());
    private Runnable lookupRunnable;

    private void setupInstituteLookup() {
        binding.etInstituteCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (lookupRunnable != null) {
                    lookupHandler.removeCallbacks(lookupRunnable);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String code = s != null ? s.toString().trim() : "";
                if (code.length() >= 2) {
                    lookupRunnable = () -> resolveInstitute(code);
                    lookupHandler.postDelayed(lookupRunnable, 400);
                } else {
                    binding.tvInstituteName.setText("HostelDesk Resident Portal");
                    binding.tvInstituteStatus.setVisibility(View.GONE);
                }
            }
        });

        // Resolve initial code
        String initialCode = binding.etInstituteCode.getText() != null ? binding.etInstituteCode.getText().toString().trim() : "";
        if (!initialCode.isEmpty()) {
            resolveInstitute(initialCode);
        }
    }

    private void resolveInstitute(String code) {
        ApiClient.getApiService(this).getInstituteByCode(code).enqueue(new Callback<InstitutePublicDto>() {
            @Override
            public void onResponse(Call<InstitutePublicDto> call, Response<InstitutePublicDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    InstitutePublicDto dto = response.body();
                    String subtitle = dto.getInstituteName();
                    if (dto.getCampusName() != null && !dto.getCampusName().isEmpty()) {
                        subtitle += " · " + dto.getCampusName();
                    }
                    binding.tvInstituteName.setText(subtitle);
                    binding.tvInstituteStatus.setVisibility(View.VISIBLE);
                    binding.tvInstituteStatus.setText("✓ " + dto.getInstituteName());
                    binding.tvInstituteStatus.setTextColor(0xFF0F6746);
                } else {
                    binding.tvInstituteName.setText("HostelDesk Resident Portal");
                    binding.tvInstituteStatus.setVisibility(View.VISIBLE);
                    binding.tvInstituteStatus.setText("⚠ Institute code not found. Check code with hostel office.");
                    binding.tvInstituteStatus.setTextColor(0xFFB43B2B);
                }
            }

            @Override
            public void onFailure(Call<InstitutePublicDto> call, Throwable t) {
                binding.tvInstituteStatus.setVisibility(View.GONE);
            }
        });
    }

    private void showRegisterGuidanceDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Register New Institution")
                .setMessage("Institution registration establishes your university campus, hostels, blocks, and warden operational desks on HostelDesk.\n\nPlease open the HostelDesk Admin application to register your institution or contact onboarding support.")
                .setPositiveButton("Got It", null)
                .show();
    }


    private void attemptLogin() {
        String instituteCode = binding.etInstituteCode.getText() != null ? binding.etInstituteCode.getText().toString().trim() : "";
        String email = binding.etEmail.getText() != null ? binding.etEmail.getText().toString().trim() : "";
        String password = binding.etPassword.getText() != null ? binding.etPassword.getText().toString().trim() : "";

        if (instituteCode.isEmpty()) {
            instituteCode = "NCH-001";
        }

        if (email.isEmpty()) {
            showError("Please enter your student email or roll number.");
            return;
        }

        if (password.isEmpty()) {
            showError("Please enter your password.");
            return;
        }

        setLoading(true);

        ApiClient.getApiService(this).login(new LoginRequest(instituteCode, email, password))
                .enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                        setLoading(false);
                        if (response.isSuccessful() && response.body() != null) {
                            LoginResponse loginResponse = response.body();
                            if (loginResponse.getUser() != null &&
                                    !"STUDENT".equalsIgnoreCase(loginResponse.getUser().getRole())) {
                                showError("Access Denied: This application is for student residents only. Staff members must use HostelDesk Admin.");
                                return;
                            }

                            sessionManager.saveSession(loginResponse.getToken(), loginResponse.getUser());
                            ApiClient.resetClient();

                            if (loginResponse.getUser() != null && Boolean.TRUE.equals(loginResponse.getUser().getNeedsPasswordChange())) {
                                showMandatoryPasswordChangeDialog(password);
                            } else {
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                            }
                        } else {
                            String errorMsg = null;
                            try {
                                if (response.errorBody() != null) {
                                    String raw = response.errorBody().string();
                                    org.json.JSONObject obj = new org.json.JSONObject(raw);
                                    if (obj.has("message")) {
                                        errorMsg = obj.getString("message");
                                    }
                                }
                            } catch (Exception ignored) {}

                            if (response.code() == 401) {
                                showError(errorMsg != null ? errorMsg : "Invalid credentials. Please verify your email and password.");
                            } else if (response.code() == 403) {
                                showError(errorMsg != null ? errorMsg : "Access denied for this account type.");
                            } else if (response.code() == 400) {
                                showError(errorMsg != null ? errorMsg : "Invalid request. Please check email/ID and password.");
                            } else {
                                showError("Server error (HTTP " + response.code() + "): " + (errorMsg != null ? errorMsg : "Ensure backend is running."));
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        setLoading(false);
                        showError("Connection error: " + t.getMessage() + "\n\n👉 Tap here to change Server URL (USB reverse or Wi-Fi)");
                    }
                });
    }

    private void showError(String message) {
        binding.tvError.setText(message);
        binding.tvError.setVisibility(View.VISIBLE);
    }

    private void setLoading(boolean loading) {
        binding.btnLogin.setEnabled(!loading);
        binding.loginProgress.setVisibility(loading ? View.VISIBLE : View.GONE);
        if (loading) {
            binding.tvError.setVisibility(View.GONE);
        }
    }

    private void showMandatoryPasswordChangeDialog(String currentPassword) {
        android.widget.LinearLayout container = new android.widget.LinearLayout(this);
        container.setOrientation(android.widget.LinearLayout.VERTICAL);
        int pad = (int) (18 * getResources().getDisplayMetrics().density);
        container.setPadding(pad, pad, pad, pad);

        android.widget.TextView tvNotice = new android.widget.TextView(this);
        tvNotice.setText("Security Notice: You are logging in with a temporary or initial password. You must establish a new permanent password to secure your account.");
        tvNotice.setTextColor(0xFFB43B2B);
        tvNotice.setTextSize(13.5f);
        tvNotice.setPadding(0, 0, 0, (int) (14 * getResources().getDisplayMetrics().density));
        container.addView(tvNotice);

        com.google.android.material.textfield.TextInputLayout tilCurrent = new com.google.android.material.textfield.TextInputLayout(this);
        tilCurrent.setHint("Current Password");
        tilCurrent.setEndIconMode(com.google.android.material.textfield.TextInputLayout.END_ICON_PASSWORD_TOGGLE);
        com.google.android.material.textfield.TextInputEditText etCurrent = new com.google.android.material.textfield.TextInputEditText(this);
        etCurrent.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        if (currentPassword != null) {
            etCurrent.setText(currentPassword);
        }
        tilCurrent.addView(etCurrent);
        container.addView(tilCurrent);

        com.google.android.material.textfield.TextInputLayout tilNew = new com.google.android.material.textfield.TextInputLayout(this);
        tilNew.setHint("New Password (min 6 characters)");
        tilNew.setEndIconMode(com.google.android.material.textfield.TextInputLayout.END_ICON_PASSWORD_TOGGLE);
        com.google.android.material.textfield.TextInputEditText etNew = new com.google.android.material.textfield.TextInputEditText(this);
        etNew.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        tilNew.addView(etNew);
        container.addView(tilNew);

        com.google.android.material.textfield.TextInputLayout tilConfirm = new com.google.android.material.textfield.TextInputLayout(this);
        tilConfirm.setHint("Confirm New Password");
        tilConfirm.setEndIconMode(com.google.android.material.textfield.TextInputLayout.END_ICON_PASSWORD_TOGGLE);
        com.google.android.material.textfield.TextInputEditText etConfirm = new com.google.android.material.textfield.TextInputEditText(this);
        etConfirm.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        tilConfirm.addView(etConfirm);
        container.addView(tilConfirm);

        androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Establish New Password")
                .setView(container)
                .setCancelable(false)
                .setPositiveButton("Update & Proceed", null)
                .setNegativeButton("Cancel / Log Out", (d, w) -> {
                    sessionManager.clearSession();
                    ApiClient.resetClient();
                })
                .create();

        dialog.setOnShowListener(d -> {
            android.widget.Button posBtn = dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE);
            posBtn.setOnClickListener(v -> {
                String curr = etCurrent.getText() != null ? etCurrent.getText().toString().trim() : "";
                String newPwd = etNew.getText() != null ? etNew.getText().toString().trim() : "";
                String confirmPwd = etConfirm.getText() != null ? etConfirm.getText().toString().trim() : "";

                if (curr.isEmpty() || newPwd.isEmpty()) {
                    android.widget.Toast.makeText(this, "Please complete all password fields", android.widget.Toast.LENGTH_SHORT).show();
                    return;
                }
                if (newPwd.length() < 6) {
                    android.widget.Toast.makeText(this, "New password must be at least 6 characters", android.widget.Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!newPwd.equals(confirmPwd)) {
                    android.widget.Toast.makeText(this, "New passwords do not match", android.widget.Toast.LENGTH_SHORT).show();
                    return;
                }

                posBtn.setEnabled(false);
                ApiClient.getApiService(this).changePassword(new com.adarshsingh.hosteldesk.data.model.ChangePasswordRequest(curr, newPwd))
                        .enqueue(new Callback<java.util.Map<String, String>>() {
                            @Override
                            public void onResponse(Call<java.util.Map<String, String>> call, Response<java.util.Map<String, String>> response) {
                                posBtn.setEnabled(true);
                                if (response.isSuccessful()) {
                                    android.widget.Toast.makeText(LoginActivity.this, "Password updated successfully!", android.widget.Toast.LENGTH_LONG).show();
                                    com.adarshsingh.hosteldesk.data.model.UserDto user = sessionManager.getUser();
                                    if (user != null) {
                                        user.setNeedsPasswordChange(false);
                                        sessionManager.saveSession(sessionManager.getToken(), user);
                                    }
                                    dialog.dismiss();
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    finish();
                                } else {
                                    String errMsg = "Failed to update password. Please check your current password.";
                                    try {
                                        if (response.errorBody() != null) {
                                            org.json.JSONObject obj = new org.json.JSONObject(response.errorBody().string());
                                            if (obj.has("message")) errMsg = obj.getString("message");
                                        }
                                    } catch (Exception ignored) {}
                                    android.widget.Toast.makeText(LoginActivity.this, errMsg, android.widget.Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<java.util.Map<String, String>> call, Throwable t) {
                                posBtn.setEnabled(true);
                                android.widget.Toast.makeText(LoginActivity.this, "Network error: " + t.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
                            }
                        });
            });
        });

        dialog.show();
    }
}
