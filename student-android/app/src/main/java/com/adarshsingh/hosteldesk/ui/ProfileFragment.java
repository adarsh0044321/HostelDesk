package com.adarshsingh.hosteldesk.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.adarshsingh.hosteldesk.data.api.ApiClient;
import com.adarshsingh.hosteldesk.data.local.SessionManager;
import com.adarshsingh.hosteldesk.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = SessionManager.getInstance(requireContext());

        setupProfileData();
        setupEmergencyDialers();
        loadEmergencyContacts();

        binding.btnEditProfile.setOnClickListener(v -> showEditProfileDialog());
        binding.btnChangePassword.setOnClickListener(v -> showChangePasswordDialog());
        binding.btnContactIt.setOnClickListener(v -> showContactItDialog());

        binding.btnLogout.setOnClickListener(v -> {
            sessionManager.clearSession();
            ApiClient.resetClient();

            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    private void setupProfileData() {
        String institute = sessionManager.getInstituteName();
        if (institute != null && !institute.isEmpty()) {
            binding.tvProfileSubtitle.setText(institute + " · Resident Portal");
        } else {
            binding.tvProfileSubtitle.setText("Campus Facilities Management");
        }

        String name = sessionManager.getFullName();
        binding.tvProfileName.setText(name != null && !name.isEmpty() ? name : "Resident Student");

        if (name != null && !name.trim().isEmpty()) {
            String[] parts = name.trim().split("\\s+");
            StringBuilder initials = new StringBuilder();
            for (String part : parts) {
                if (!part.isEmpty()) initials.append(part.charAt(0));
                if (initials.length() >= 2) break;
            }
            binding.tvAvatarInitials.setText(initials.toString().toUpperCase());
        } else {
            binding.tvAvatarInitials.setText("R");
        }

        String studentId = sessionManager.getStudentId();
        binding.tvProfileRollNumber.setText("ID: " + (studentId != null && !studentId.isEmpty() ? studentId : "--"));

        String email = sessionManager.getEmail();
        binding.tvProfileEmail.setText(email != null && !email.isEmpty() ? email : "--");

        String hostel = sessionManager.getHostelName();
        binding.tvHostelBlock.setText(hostel != null && !hostel.isEmpty() ? hostel : "Campus Residence");

        String room = sessionManager.getRoomNumber();
        binding.tvRoomNumber.setText(room != null && !room.isEmpty() ? (room.startsWith("Room") ? room : "Room " + room) : "Unassigned");

        String batch = sessionManager.getBatch();
        binding.tvProfileBatch.setText(batch != null && !batch.isEmpty() ? batch : "--");
    }

    private void setupEmergencyDialers() {
        binding.rowWardenEmergency.setOnClickListener(v -> dialPhone(binding.tvWardenContact.getText().toString()));
        binding.rowSecurityEmergency.setOnClickListener(v -> dialPhone(binding.tvSecurityContact.getText().toString()));
        binding.rowMedicalEmergency.setOnClickListener(v -> dialPhone(binding.tvMedicalContact.getText().toString()));
    }

    private void dialPhone(String number) {
        if (number == null || number.trim().isEmpty()) return;
        try {
            Intent intent = new Intent(Intent.ACTION_DIAL, android.net.Uri.parse("tel:" + number.trim()));
            startActivity(intent);
        } catch (Exception e) {
            // Dialer not available (e.g. tablet or emulator without tel handler)
        }
    }

    private void loadEmergencyContacts() {
        ApiClient.getApiService(requireContext()).getEmergencyContacts()
                .enqueue(new retrofit2.Callback<com.adarshsingh.hosteldesk.data.model.EmergencyContactsDto>() {
                    @Override
                    public void onResponse(retrofit2.Call<com.adarshsingh.hosteldesk.data.model.EmergencyContactsDto> call,
                                           retrofit2.Response<com.adarshsingh.hosteldesk.data.model.EmergencyContactsDto> response) {
                        if (!isAdded() || binding == null) return;
                        if (response.isSuccessful() && response.body() != null) {
                            com.adarshsingh.hosteldesk.data.model.EmergencyContactsDto c = response.body();
                            if (c.getAmbulanceContact() != null && !c.getAmbulanceContact().trim().isEmpty()) {
                                binding.tvMedicalContact.setText(c.getAmbulanceContact().trim());
                            }
                            if (c.getSecurityContact() != null && !c.getSecurityContact().trim().isEmpty()) {
                                binding.tvSecurityContact.setText(c.getSecurityContact().trim());
                            }
                            String wardenNum = (c.getDutyWardenPhone() != null && !c.getDutyWardenPhone().trim().isEmpty())
                                    ? c.getDutyWardenPhone()
                                    : c.getEmergencyDeskContact();
                            if (wardenNum != null && !wardenNum.trim().isEmpty()) {
                                binding.tvWardenContact.setText(wardenNum.trim());
                            }
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<com.adarshsingh.hosteldesk.data.model.EmergencyContactsDto> call, Throwable t) {}
                });
    }

    private void showEditProfileDialog() {
        android.widget.LinearLayout layout = new android.widget.LinearLayout(requireContext());
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        int pad = (int) (18 * getResources().getDisplayMetrics().density);
        layout.setPadding(pad, pad / 2, pad, pad / 2);

        final android.widget.EditText etName = new android.widget.EditText(requireContext());
        etName.setHint("Full Name");
        etName.setText(sessionManager.getFullName());
        layout.addView(etName);

        final android.widget.EditText etRoom = new android.widget.EditText(requireContext());
        etRoom.setHint("Room Number (e.g. Room 009)");
        etRoom.setText(sessionManager.getRoomNumber());
        layout.addView(etRoom);

        final android.widget.EditText etPhone = new android.widget.EditText(requireContext());
        etPhone.setHint("Contact Phone Number");
        etPhone.setInputType(android.text.InputType.TYPE_CLASS_PHONE);
        layout.addView(etPhone);

        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Update Profile Details")
                .setView(layout)
                .setPositiveButton("Save Changes", (d, w) -> {
                    String name = etName.getText().toString().trim();
                    String room = etRoom.getText().toString().trim();
                    String phone = etPhone.getText().toString().trim();

                    com.adarshsingh.hosteldesk.data.model.UpdateProfileRequest req =
                            new com.adarshsingh.hosteldesk.data.model.UpdateProfileRequest(
                                    name.isEmpty() ? null : name,
                                    phone.isEmpty() ? null : phone,
                                    room.isEmpty() ? null : room
                            );

                    ApiClient.getApiService(requireContext()).updateProfile(req)
                            .enqueue(new retrofit2.Callback<com.adarshsingh.hosteldesk.data.model.UserDto>() {
                                @Override
                                public void onResponse(retrofit2.Call<com.adarshsingh.hosteldesk.data.model.UserDto> call,
                                                       retrofit2.Response<com.adarshsingh.hosteldesk.data.model.UserDto> response) {
                                    if (response.isSuccessful() && response.body() != null) {
                                        com.adarshsingh.hosteldesk.data.model.UserDto u = response.body();
                                        sessionManager.updateUserProfile(u.getFullName(), u.getRoomNumber());
                                        setupProfileData();
                                        android.widget.Toast.makeText(requireContext(), "Profile updated successfully!", android.widget.Toast.LENGTH_SHORT).show();
                                    } else {
                                        android.widget.Toast.makeText(requireContext(), "Failed to update profile", android.widget.Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(retrofit2.Call<com.adarshsingh.hosteldesk.data.model.UserDto> call, Throwable t) {
                                    android.widget.Toast.makeText(requireContext(), "Network error: " + t.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showChangePasswordDialog() {
        android.widget.LinearLayout layout = new android.widget.LinearLayout(requireContext());
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        int pad = (int) (18 * getResources().getDisplayMetrics().density);
        layout.setPadding(pad, pad / 2, pad, pad / 2);

        final android.widget.EditText etOld = new android.widget.EditText(requireContext());
        etOld.setHint("Current Password");
        etOld.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(etOld);

        final android.widget.EditText etNew = new android.widget.EditText(requireContext());
        etNew.setHint("New Password (min 6 chars)");
        etNew.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(etNew);

        final android.widget.EditText etConfirm = new android.widget.EditText(requireContext());
        etConfirm.setHint("Confirm New Password");
        etConfirm.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(etConfirm);

        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Change Account Password")
                .setView(layout)
                .setPositiveButton("Change Password", (d, w) -> {
                    String oldPass = etOld.getText().toString().trim();
                    String newPass = etNew.getText().toString().trim();
                    String confirmPass = etConfirm.getText().toString().trim();

                    if (newPass.length() < 6) {
                        android.widget.Toast.makeText(requireContext(), "New password must be at least 6 characters.", android.widget.Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (!newPass.equals(confirmPass)) {
                        android.widget.Toast.makeText(requireContext(), "Passwords do not match.", android.widget.Toast.LENGTH_LONG).show();
                        return;
                    }

                    com.adarshsingh.hosteldesk.data.model.ChangePasswordRequest req =
                            new com.adarshsingh.hosteldesk.data.model.ChangePasswordRequest(oldPass, newPass);

                    ApiClient.getApiService(requireContext()).changePassword(req)
                            .enqueue(new retrofit2.Callback<java.util.Map<String, String>>() {
                                @Override
                                public void onResponse(retrofit2.Call<java.util.Map<String, String>> call,
                                                       retrofit2.Response<java.util.Map<String, String>> response) {
                                    if (response.isSuccessful()) {
                                        android.widget.Toast.makeText(requireContext(), "Password changed successfully!", android.widget.Toast.LENGTH_SHORT).show();
                                    } else {
                                        android.widget.Toast.makeText(requireContext(), "Failed to change password. Please check your current password.", android.widget.Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onFailure(retrofit2.Call<java.util.Map<String, String>> call, Throwable t) {
                                    android.widget.Toast.makeText(requireContext(), "Network error: " + t.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showContactItDialog() {
        android.widget.LinearLayout layout = new android.widget.LinearLayout(requireContext());
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        int pad = (int) (18 * getResources().getDisplayMetrics().density);
        layout.setPadding(pad, pad / 2, pad, pad / 2);

        android.widget.TextView tvInfo = new android.widget.TextView(requireContext());
        tvInfo.setText("Campus IT Helpdesk & Account Recovery:\n\n" +
                "• IT Helpline: +91 11 2766 7722\n" +
                "• Support Email: itdesk@campus.edu\n" +
                "• Location: Admin Block, Desk Room 102\n\n" +
                "Need a password reset? Submit a ticket request below:");
        tvInfo.setTextColor(0xFF334155);
        tvInfo.setTextSize(13);
        layout.addView(tvInfo);

        final android.widget.EditText etReason = new android.widget.EditText(requireContext());
        etReason.setHint("State reason (e.g. forgot password, account locked)");
        etReason.setTextSize(13);
        layout.addView(etReason);

        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("IT Department Assistance")
                .setView(layout)
                .setPositiveButton("Submit Reset Ticket", (d, w) -> {
                    String reason = etReason.getText().toString().trim();
                    com.adarshsingh.hosteldesk.data.model.ForgotPasswordRequest req =
                            new com.adarshsingh.hosteldesk.data.model.ForgotPasswordRequest(
                                    sessionManager.getInstituteCode(),
                                    sessionManager.getStudentId(),
                                    reason.isEmpty() ? "Resident password recovery requested" : reason,
                                    null
                            );

                    ApiClient.getApiService(requireContext()).forgotPassword(req)
                            .enqueue(new retrofit2.Callback<java.util.Map<String, String>>() {
                                @Override
                                public void onResponse(retrofit2.Call<java.util.Map<String, String>> call,
                                                       retrofit2.Response<java.util.Map<String, String>> response) {
                                    if (response.isSuccessful()) {
                                        android.widget.Toast.makeText(requireContext(), "Password reset request submitted to IT Admin!", android.widget.Toast.LENGTH_LONG).show();
                                    } else {
                                        android.widget.Toast.makeText(requireContext(), "Failed to submit request", android.widget.Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(retrofit2.Call<java.util.Map<String, String>> call, Throwable t) {
                                    android.widget.Toast.makeText(requireContext(), "Network error: " + t.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .setNegativeButton("Close", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

