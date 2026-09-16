package com.adarshsingh.adminhosteldesk.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import com.adarshsingh.adminhosteldesk.data.api.ApiClient;
import com.adarshsingh.adminhosteldesk.data.local.SessionManager;
import com.adarshsingh.adminhosteldesk.data.model.InstitutePublicDto;
import com.adarshsingh.adminhosteldesk.data.model.LoginRequest;
import com.adarshsingh.adminhosteldesk.data.model.LoginResponse;
import com.adarshsingh.adminhosteldesk.data.model.RegisterInstituteRequest;
import com.adarshsingh.adminhosteldesk.databinding.ActivityAdminLoginBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminLoginActivity extends AppCompatActivity {


    private ActivityAdminLoginBinding binding;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = SessionManager.getInstance(this);

        setupInstituteLookup();

        binding.ivLogo.setOnClickListener(v -> showInstituteAdminAccessDialog());
        binding.btnExecutivePortal.setOnClickListener(v -> showInstituteAdminAccessDialog());
        binding.tvResetCode.setOnClickListener(v -> showForgotPasswordDialog());
        binding.btnRegisterInstitute.setOnClickListener(v -> showRegisterInstituteDialog());
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
        etIdent.setHint("Staff Email or ID");
        etIdent.setText(binding.etEmail.getText() != null ? binding.etEmail.getText().toString().trim() : "");
        container.addView(etIdent);

        android.widget.EditText etPhone = new android.widget.EditText(this);
        etPhone.setHint("Contact Phone Number (for IT Desk callback)");
        etPhone.setInputType(android.text.InputType.TYPE_CLASS_PHONE);
        container.addView(etPhone);

        android.widget.EditText etReason = new android.widget.EditText(this);
        etReason.setHint("Reason / Issue (e.g. Forgotten password, device reset)");
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
                        android.widget.Toast.makeText(this, "Institute ID and identifier are required", android.widget.Toast.LENGTH_SHORT).show();
                        return;
                    }

                    com.adarshsingh.adminhosteldesk.data.model.ForgotPasswordRequest req =
                            new com.adarshsingh.adminhosteldesk.data.model.ForgotPasswordRequest(inst, ident, "WARDEN", reason, phone);

                    ApiClient.getApiService(this).forgotPassword(req).enqueue(new Callback<java.util.Map<String, String>>() {
                        @Override
                        public void onResponse(Call<java.util.Map<String, String>> call, Response<java.util.Map<String, String>> response) {
                            if (response.isSuccessful()) {
                                String contactMsg = phone.isEmpty() ? "" : ("\n\nAn IT officer will contact you on " + phone + " to verify your identity.");
                                new androidx.appcompat.app.AlertDialog.Builder(AdminLoginActivity.this)
                                        .setTitle("Request Routed to Institute IT")
                                        .setMessage("Reset request logged successfully. Routed to the Institute IT & Administration Desk." + contactMsg)
                                        .setPositiveButton("OK", null)
                                        .show();
                            } else {
                                android.widget.Toast.makeText(AdminLoginActivity.this, "Failed to submit request (" + response.code() + ")", android.widget.Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<java.util.Map<String, String>> call, Throwable t) {
                            android.widget.Toast.makeText(AdminLoginActivity.this, "Error: " + t.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showInstituteAdminAccessDialog() {
        String[] options = new String[]{
                "Log in with your Institute Code"
        };

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Institute Administrator Access")
                .setIcon(com.adarshsingh.adminhosteldesk.R.drawable.ic_security)
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        showExecutivePortalDialog();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showExecutivePortalDialog() {
        android.widget.ScrollView scrollView = new android.widget.ScrollView(this);
        android.widget.LinearLayout container = new android.widget.LinearLayout(this);
        container.setOrientation(android.widget.LinearLayout.VERTICAL);
        int pad = (int) (16 * getResources().getDisplayMetrics().density);
        container.setPadding(pad, pad, pad, pad);
        scrollView.addView(container);

        android.widget.TextView tvTitle = new android.widget.TextView(this);
        tvTitle.setText("🏛️ Institute Executive Portal");
        tvTitle.setTextSize(17);
        tvTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        tvTitle.setTextColor(0xFF1A1C1F);
        container.addView(tvTitle);

        android.widget.TextView tvSubtitle = new android.widget.TextView(this);
        tvSubtitle.setText("Multi-factor verification for Institutional Directors and Executive Administrators only.");
        tvSubtitle.setTextSize(12);
        tvSubtitle.setTextColor(0xFF515F74);
        tvSubtitle.setPadding(0, 4, 0, pad / 2);
        container.addView(tvSubtitle);

        // Institute Code Input
        android.widget.EditText etInstCode = new android.widget.EditText(this);
        etInstCode.setHint("Institute Code (e.g. JAI, NCH-001)");
        String currCode = binding.etInstituteCode.getText() != null ? binding.etInstituteCode.getText().toString().trim() : "";
        etInstCode.setText(currCode);
        container.addView(etInstCode);

        // Status banner
        android.widget.TextView tvVerifiedBadge = new android.widget.TextView(this);
        tvVerifiedBadge.setTextSize(11);
        tvVerifiedBadge.setTypeface(null, android.graphics.Typeface.BOLD);
        tvVerifiedBadge.setPadding(0, 2, 0, 8);
        container.addView(tvVerifiedBadge);

        // Realtime verify
        Runnable checkCode = () -> {
            String c = etInstCode.getText().toString().trim();
            if (c.length() >= 2) {
                ApiClient.getApiService(this).getInstituteByCode(c).enqueue(new Callback<InstitutePublicDto>() {
                    @Override
                    public void onResponse(Call<InstitutePublicDto> call, Response<InstitutePublicDto> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            tvVerifiedBadge.setVisibility(View.VISIBLE);
                            tvVerifiedBadge.setText("✓ Verified Institute: " + response.body().getInstituteName());
                            tvVerifiedBadge.setTextColor(0xFF0F6746);
                        } else {
                            tvVerifiedBadge.setVisibility(View.VISIBLE);
                            tvVerifiedBadge.setText("⚠ Unverified institute code");
                            tvVerifiedBadge.setTextColor(0xFFB43B2B);
                        }
                    }

                    @Override
                    public void onFailure(Call<InstitutePublicDto> call, Throwable t) {
                        tvVerifiedBadge.setVisibility(View.GONE);
                    }
                });
            }
        };
        etInstCode.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            public void afterTextChanged(Editable s) { checkCode.run(); }
        });
        if (!currCode.isEmpty()) checkCode.run();

        // Administrator Email or Executive ID
        android.widget.EditText etAdminId = new android.widget.EditText(this);
        etAdminId.setHint("Executive Admin Email / Master ID (e.g. adminjai)");
        container.addView(etAdminId);

        // Security Password
        android.widget.EditText etAdminPass = new android.widget.EditText(this);
        etAdminPass.setHint("Master Security Password");
        etAdminPass.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        container.addView(etAdminPass);

        // Complex Security Challenge Section: Third Security Code / Secret PIN
        android.widget.TextView tvChallengeLabel = new android.widget.TextView(this);
        tvChallengeLabel.setText("3rd Factor: Executive Security Code / Secret PIN");
        tvChallengeLabel.setTextSize(12);
        tvChallengeLabel.setTypeface(null, android.graphics.Typeface.BOLD);
        tvChallengeLabel.setTextColor(0xFFBA5333);
        tvChallengeLabel.setPadding(0, pad / 2, 0, 4);
        container.addView(tvChallengeLabel);

        android.widget.EditText etChallengeAnswer = new android.widget.EditText(this);
        etChallengeAnswer.setHint("Enter 3rd Security Passcode PIN (e.g. 112233)");
        etChallengeAnswer.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        container.addView(etChallengeAnswer);

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setView(scrollView)
                .setPositiveButton("Verify & Sign In", (dialog, which) -> {
                    String inst = etInstCode.getText().toString().trim();
                    String ident = etAdminId.getText().toString().trim();
                    String pass = etAdminPass.getText().toString().trim();
                    String answer = etChallengeAnswer.getText().toString().trim();

                    if (inst.isEmpty() || ident.isEmpty() || pass.isEmpty() || answer.isEmpty()) {
                        android.widget.Toast.makeText(this, "All 4 credentials are required: Institute Code, Email/ID, Password, and Executive Security PIN", android.widget.Toast.LENGTH_LONG).show();
                        return;
                    }

                    setLoading(true);
                    ApiClient.getApiService(this).login(new LoginRequest(inst, ident, pass, answer, "EXECUTIVE_PORTAL"))
                            .enqueue(new Callback<LoginResponse>() {
                                @Override
                                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                                    setLoading(false);
                                    if (response.isSuccessful() && response.body() != null) {
                                        LoginResponse loginResponse = response.body();
                                        com.adarshsingh.adminhosteldesk.data.model.UserDto user = loginResponse.getUser();

                                        if (user == null || (!"INSTITUTE_ADMIN".equalsIgnoreCase(user.getRole())
                                                && !"ADMIN".equalsIgnoreCase(user.getRole())
                                                && !"SUPER_ADMIN".equalsIgnoreCase(user.getRole()))) {
                                            new androidx.appcompat.app.AlertDialog.Builder(AdminLoginActivity.this)
                                                    .setTitle("Access Restricted")
                                                    .setIcon(com.adarshsingh.adminhosteldesk.R.drawable.ic_security)
                                                    .setMessage("Access Denied: This portal requires verified Institute Administrator clearance. Wardens and Maintenance Staff must sign in from the main operational desk.")
                                                    .setPositiveButton("OK", null)
                                                    .show();
                                            return;
                                        }

                                        sessionManager.saveSession(loginResponse.getToken(), user);
                                        ApiClient.resetClient();

                                        if (Boolean.TRUE.equals(user.getNeedsPasswordChange())) {
                                            showMandatoryPasswordChangeDialog(pass);
                                        } else {
                                            startActivity(new Intent(AdminLoginActivity.this, AdminMainActivity.class));
                                            finish();
                                        }
                                    } else {
                                        android.widget.Toast.makeText(AdminLoginActivity.this, "Executive Authentication failed. Invalid credentials or security clearance.", android.widget.Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<LoginResponse> call, Throwable t) {
                                    setLoading(false);
                                    android.widget.Toast.makeText(AdminLoginActivity.this, "Connection error: " + t.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
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
                    binding.tvAdminSubtitle.setText("Campus Operations & Administrative Portal");
                    binding.tvAdminGreetingSubtitle.setText("Sign in with your institutional credentials");
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
                    String subtitle = dto.getInstituteName() + " Operations Desk";
                    binding.tvAdminSubtitle.setText(subtitle);
                    binding.tvAdminGreetingSubtitle.setText(dto.getInstituteName() + " · Administrative Portal");
                    binding.tvInstituteStatus.setVisibility(View.VISIBLE);
                    binding.tvInstituteStatus.setText("✓ Verified: " + dto.getInstituteName());
                    binding.tvInstituteStatus.setTextColor(0xFF0F6746);
                } else {
                    binding.tvAdminSubtitle.setText("Campus Operations & Administrative Portal");
                    binding.tvAdminGreetingSubtitle.setText("Sign in with your institutional credentials");
                    binding.tvInstituteStatus.setVisibility(View.VISIBLE);
                    binding.tvInstituteStatus.setText("⚠ Institute code not found. Register below.");
                    binding.tvInstituteStatus.setTextColor(0xFFB43B2B);
                }
            }

            @Override
            public void onFailure(Call<InstitutePublicDto> call, Throwable t) {
                binding.tvInstituteStatus.setVisibility(View.GONE);
            }
        });
    }

    private void showRegisterInstituteDialog() {
        android.widget.ScrollView scrollView = new android.widget.ScrollView(this);
        android.widget.LinearLayout container = new android.widget.LinearLayout(this);
        container.setOrientation(android.widget.LinearLayout.VERTICAL);
        int pad = (int) (16 * getResources().getDisplayMetrics().density);
        container.setPadding(pad, pad, pad, pad);
        scrollView.addView(container);

        android.widget.TextView tvTitle = new android.widget.TextView(this);
        tvTitle.setText("Register Institution & Setup Campus");
        tvTitle.setTextSize(16);
        tvTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        tvTitle.setTextColor(0xFF1A1C1F);
        tvTitle.setPadding(0, 0, 0, pad / 2);
        container.addView(tvTitle);

        android.widget.EditText etInstName = new android.widget.EditText(this);
        etInstName.setHint("Institution Name (e.g. National Institute of Tech)");
        container.addView(etInstName);

        android.widget.EditText etInstCode = new android.widget.EditText(this);
        etInstCode.setHint("Desired Institute Code (e.g. NIT-001)");
        container.addView(etInstCode);

        android.widget.EditText etContact = new android.widget.EditText(this);
        etContact.setHint("Campus Duty / Helpline Phone");
        etContact.setInputType(android.text.InputType.TYPE_CLASS_PHONE);
        container.addView(etContact);

        android.widget.EditText etAdminName = new android.widget.EditText(this);
        etAdminName.setHint("Administrator Full Name");
        container.addView(etAdminName);

        android.widget.EditText etAdminEmail = new android.widget.EditText(this);
        etAdminEmail.setHint("Administrator Official Email");
        etAdminEmail.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        container.addView(etAdminEmail);

        android.widget.EditText etAdminPass = new android.widget.EditText(this);
        etAdminPass.setHint("Administrator Password (min 6 chars)");
        etAdminPass.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        container.addView(etAdminPass);

        // 3rd Factor: Security Passcode / Secret PIN
        android.widget.LinearLayout pinLayout = new android.widget.LinearLayout(this);
        pinLayout.setOrientation(android.widget.LinearLayout.VERTICAL);
        pinLayout.setPadding(0, 8, 0, 0);

        android.widget.TextView tvPinLabel = new android.widget.TextView(this);
        tvPinLabel.setText("Executive Security PIN / Passcode (3rd Factor):");
        tvPinLabel.setTextSize(12);
        tvPinLabel.setTypeface(null, android.graphics.Typeface.BOLD);
        tvPinLabel.setTextColor(0xFF0F6746);
        pinLayout.addView(tvPinLabel);

        android.widget.LinearLayout pinInputRow = new android.widget.LinearLayout(this);
        pinInputRow.setOrientation(android.widget.LinearLayout.HORIZONTAL);
        pinInputRow.setGravity(android.view.Gravity.CENTER_VERTICAL);

        android.widget.EditText etSecurityPin = new android.widget.EditText(this);
        etSecurityPin.setHint("Custom 6-digit PIN (or tap Re-generate)");
        etSecurityPin.setText(String.format("%06d", (int)(Math.random() * 900000) + 100000));
        etSecurityPin.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        etSecurityPin.setLayoutParams(new android.widget.LinearLayout.LayoutParams(0, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
        pinInputRow.addView(etSecurityPin);

        android.widget.Button btnAutoPin = new android.widget.Button(this);
        btnAutoPin.setText("Re-generate");
        btnAutoPin.setTextSize(11);
        btnAutoPin.setOnClickListener(v -> {
            String randomPin = String.format("%06d", (int)(Math.random() * 900000) + 100000);
            etSecurityPin.setText(randomPin);
        });
        pinInputRow.addView(btnAutoPin);

        pinLayout.addView(pinInputRow);
        container.addView(pinLayout);

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setView(scrollView)
                .setPositiveButton("Register & Onboard", (dialog, which) -> {
                    String name = etInstName.getText().toString().trim();
                    String code = etInstCode.getText().toString().trim().toUpperCase();
                    String contact = etContact.getText().toString().trim();
                    String adminName = etAdminName.getText().toString().trim();
                    String adminEmail = etAdminEmail.getText().toString().trim();
                    String pass = etAdminPass.getText().toString().trim();
                    String pin = etSecurityPin.getText().toString().trim();

                    if (name.isEmpty() || adminName.isEmpty() || adminEmail.isEmpty() || pass.isEmpty()) {
                        android.widget.Toast.makeText(this, "Please fill all required registration fields", android.widget.Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (pin.isEmpty()) {
                        pin = String.format("%06d", (int)(Math.random() * 900000) + 100000);
                    }
                    final String assignedPin = pin;

                    RegisterInstituteRequest req = new RegisterInstituteRequest(name, code.isEmpty() ? null : code, contact, adminName, adminEmail, pass, pin);

                    binding.loginProgress.setVisibility(View.VISIBLE);
                    ApiClient.getApiService(this).registerInstitute(req).enqueue(new Callback<LoginResponse>() {
                        @Override
                        public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                            binding.loginProgress.setVisibility(View.GONE);
                            if (response.isSuccessful() && response.body() != null) {
                                LoginResponse loginRes = response.body();
                                String assignedCode = loginRes.getUser().getInstituteCode();
                                binding.etInstituteCode.setText(assignedCode);
                                binding.etEmail.setText(adminEmail);
                                binding.etPassword.setText(pass);
                                resolveInstitute(assignedCode);

                                new androidx.appcompat.app.AlertDialog.Builder(AdminLoginActivity.this)
                                        .setTitle("Institution Registered!")
                                        .setMessage("Institute '" + name + "' is registered successfully with ID: " + assignedCode + ".\n\n"
                                                + "• Admin Account: " + adminEmail + "\n"
                                                + "• 3rd Factor PIN: " + assignedPin + "\n\n"
                                                + "IMPORTANT: Keep this 3rd Factor PIN safe. It is required to log into the Executive Portal.")
                                        .setPositiveButton("Open Executive Portal", (d, w) -> {
                                            binding.etInstituteCode.setText(assignedCode);
                                            showExecutivePortalDialog();
                                        })
                                        .setNegativeButton("Close", null)
                                        .show();
                            } else {
                                String msg = "Registration failed (" + response.code() + ")";
                                try {
                                    if (response.errorBody() != null) {
                                        String raw = response.errorBody().string();
                                        try {
                                            org.json.JSONObject obj = new org.json.JSONObject(raw);
                                            if (obj.has("message")) {
                                                msg = obj.getString("message");
                                            } else if (obj.has("error")) {
                                                msg = obj.getString("error");
                                            } else {
                                                msg = raw;
                                            }
                                        } catch (Exception e) {
                                            msg = raw;
                                        }
                                    }
                                } catch (Exception ignored) {}
                                android.widget.Toast.makeText(AdminLoginActivity.this, msg, android.widget.Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<LoginResponse> call, Throwable t) {
                            binding.loginProgress.setVisibility(View.GONE);
                            android.widget.Toast.makeText(AdminLoginActivity.this, "Network error: " + t.getMessage(), android.widget.Toast.LENGTH_LONG).show();
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
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
            showError("Please enter your staff or warden email address.");
            return;
        }

        if (password.isEmpty()) {
            showError("Please enter your password.");
            return;
        }

        setLoading(true);

        ApiClient.getApiService(this).login(new LoginRequest(instituteCode, email, password, null, "WARDEN_PORTAL"))
                .enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                        setLoading(false);
                        if (response.isSuccessful() && response.body() != null) {
                            LoginResponse loginResponse = response.body();

                            // Role validation: Strictly reject student logins from Admin app
                            if (loginResponse.getUser() != null &&
                                    "STUDENT".equalsIgnoreCase(loginResponse.getUser().getRole())) {
                                showError("Access Denied: Student accounts cannot access the Facilities & Warden Console. Please use HostelDesk Student.");
                                return;
                            }

                            // Role validation: Strictly reject Institute Admins from regular operational login
                            if (loginResponse.getUser() != null &&
                                    ("INSTITUTE_ADMIN".equalsIgnoreCase(loginResponse.getUser().getRole())
                                            || "SUPER_ADMIN".equalsIgnoreCase(loginResponse.getUser().getRole())
                                            || "ADMIN".equalsIgnoreCase(loginResponse.getUser().getRole()))) {
                                showError("Access Denied: Institute Administrators must sign in exclusively via the Executive Portal. Tap 'Executive Portal' below.");
                                return;
                            }

                            sessionManager.saveSession(loginResponse.getToken(), loginResponse.getUser());
                            ApiClient.resetClient();

                            if (loginResponse.getUser() != null && Boolean.TRUE.equals(loginResponse.getUser().getNeedsPasswordChange())) {
                                showMandatoryPasswordChangeDialog(password);
                            } else {
                                startActivity(new Intent(AdminLoginActivity.this, AdminMainActivity.class));
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
                                showError(errorMsg != null ? errorMsg : "Invalid credentials. Please verify your staff email and password.");
                            } else if (response.code() == 403) {
                                showError(errorMsg != null ? errorMsg : "Access denied: Account lacks required administrative privileges.");
                            } else if (response.code() == 400) {
                                showError(errorMsg != null ? errorMsg : "Invalid request. Please check staff ID/email and password.");
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
        tvNotice.setText("Security Notice: You are logging in with a temporary or default password. You must establish a new permanent password to secure your facilities console account.");
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
                ApiClient.getApiService(this).changePassword(new com.adarshsingh.adminhosteldesk.data.model.ChangePasswordRequest(curr, newPwd))
                        .enqueue(new Callback<java.util.Map<String, String>>() {
                            @Override
                            public void onResponse(Call<java.util.Map<String, String>> call, Response<java.util.Map<String, String>> response) {
                                posBtn.setEnabled(true);
                                if (response.isSuccessful()) {
                                    android.widget.Toast.makeText(AdminLoginActivity.this, "Password updated successfully!", android.widget.Toast.LENGTH_LONG).show();
                                    com.adarshsingh.adminhosteldesk.data.model.UserDto user = sessionManager.getUser();
                                    if (user != null) {
                                        user.setNeedsPasswordChange(false);
                                        sessionManager.saveSession(sessionManager.getToken(), user);
                                    }
                                    dialog.dismiss();
                                    startActivity(new Intent(AdminLoginActivity.this, AdminMainActivity.class));
                                    finish();
                                } else {
                                    String errMsg = "Failed to update password. Please check your current password.";
                                    try {
                                        if (response.errorBody() != null) {
                                            org.json.JSONObject obj = new org.json.JSONObject(response.errorBody().string());
                                            if (obj.has("message")) errMsg = obj.getString("message");
                                        }
                                    } catch (Exception ignored) {}
                                    android.widget.Toast.makeText(AdminLoginActivity.this, errMsg, android.widget.Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<java.util.Map<String, String>> call, Throwable t) {
                                posBtn.setEnabled(true);
                                android.widget.Toast.makeText(AdminLoginActivity.this, "Network error: " + t.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
                            }
                        });
            });
        });

        dialog.show();
    }
}
