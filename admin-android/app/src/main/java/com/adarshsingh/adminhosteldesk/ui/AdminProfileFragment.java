package com.adarshsingh.adminhosteldesk.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.adarshsingh.adminhosteldesk.data.api.ApiClient;
import com.adarshsingh.adminhosteldesk.data.local.SessionManager;
import com.adarshsingh.adminhosteldesk.databinding.FragmentAdminProfileBinding;

public class AdminProfileFragment extends Fragment {

    private FragmentAdminProfileBinding binding;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAdminProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = SessionManager.getInstance(requireContext());

        setupProfile();

        String role = sessionManager.getRole();
        boolean isWarden = "WARDEN".equalsIgnoreCase(role);
        boolean isInstituteAdmin = role == null || "INSTITUTE_ADMIN".equalsIgnoreCase(role) || "ADMIN".equalsIgnoreCase(role) || "SUPER_ADMIN".equalsIgnoreCase(role);

        binding.cardInstituteAdmin.setVisibility((isInstituteAdmin || isWarden) ? View.VISIBLE : View.GONE);

        if (isWarden) {
            // Strict Warden Boundaries: Only onboard staff and post hostel notices
            binding.btnOnboardStudent.setVisibility(View.GONE);
            binding.btnOnboardWarden.setVisibility(View.GONE);
            binding.btnCreateHostel.setVisibility(View.GONE);
            binding.btnPasswordResets.setVisibility(View.GONE);
            binding.btnConfigureEmergencyContacts.setVisibility(View.GONE);

            binding.btnOnboardStaff.setVisibility(View.VISIBLE);
            binding.btnPostAnnouncement.setVisibility(View.VISIBLE);
            binding.btnPostAnnouncement.setText("📢  Post Hostel Notice");

            binding.btnUpdateContact.setVisibility(View.VISIBLE);
            binding.btnUpdateContact.setOnClickListener(v ->
                    InstituteConsoleHelper.showUpdateWardenContactDialog(requireContext(), this::setupProfile)
            );
        } else {
            // Institute Admin: Full authority
            binding.btnOnboardStudent.setVisibility(View.VISIBLE);
            binding.btnOnboardWarden.setVisibility(View.VISIBLE);
            binding.btnCreateHostel.setVisibility(View.VISIBLE);
            binding.btnPasswordResets.setVisibility(View.VISIBLE);
            binding.btnConfigureEmergencyContacts.setVisibility(View.VISIBLE);

            binding.btnOnboardStaff.setVisibility(View.VISIBLE);
            binding.btnPostAnnouncement.setVisibility(View.VISIBLE);
            binding.btnPostAnnouncement.setText("📢  Broadcast Campus Announcement");

            binding.btnUpdateContact.setVisibility(View.GONE);
        }

        binding.btnOnboardStudent.setOnClickListener(v -> InstituteConsoleHelper.showOnboardStudentDialog(requireContext(), null));
        binding.btnOnboardWarden.setOnClickListener(v -> InstituteConsoleHelper.showOnboardWardenDialog(requireContext(), null));
        binding.btnOnboardStaff.setOnClickListener(v -> InstituteConsoleHelper.showOnboardStaffDialog(requireContext(), null));
        binding.btnCreateHostel.setOnClickListener(v -> InstituteConsoleHelper.showCreateHostelDialog(requireContext(), null));
        binding.btnPasswordResets.setOnClickListener(v -> InstituteConsoleHelper.showPasswordResetsDialog(requireContext()));
        binding.btnPostAnnouncement.setOnClickListener(v -> InstituteConsoleHelper.showPostAnnouncementDialog(requireContext(), isWarden));
        binding.btnManageAnnouncements.setOnClickListener(v -> InstituteConsoleHelper.showManageNoticesDialog(requireContext(), isWarden));
        binding.btnWorkersDirectory.setOnClickListener(v -> {
            WorkersDirectoryBottomSheet sheet = WorkersDirectoryBottomSheet.newInstance("ALL");
            sheet.show(getChildFragmentManager(), "WorkersDirectoryBottomSheet");
        });
        binding.btnConfigureEmergencyContacts.setOnClickListener(v -> InstituteConsoleHelper.showEmergencyContactsDialog(requireContext()));
        binding.btnChangePassword.setOnClickListener(v -> InstituteConsoleHelper.showChangePasswordDialog(requireContext()));

        binding.btnAdminLogout.setOnClickListener(v -> {
            sessionManager.clearSession();
            ApiClient.resetClient();

            Intent intent = new Intent(requireContext(), AdminLoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    private void setupProfile() {
        String name = sessionManager.getFullName();
        binding.tvStaffFullName.setText(name != null && !name.trim().isEmpty() ? name : "Staff Member");

        if (name != null && !name.trim().isEmpty()) {
            String[] parts = name.trim().split("\\s+");
            StringBuilder initials = new StringBuilder();
            for (String part : parts) {
                if (!part.isEmpty()) initials.append(part.charAt(0));
                if (initials.length() >= 2) break;
            }
            binding.tvStaffAvatar.setText(initials.toString().toUpperCase());
        } else {
            binding.tvStaffAvatar.setText("ST");
        }

        String hostel = sessionManager.getHostelName();
        String inst = sessionManager.getInstituteName();
        String campus = (hostel != null && !hostel.trim().isEmpty()) ? hostel :
                ((inst != null && !inst.trim().isEmpty()) ? inst : "Campus Facilities");
        binding.tvProfileSubtitle.setText(campus + " · Operations Console");

        String role = sessionManager.getRole();
        binding.tvStaffRoleBadge.setText("Role: " + (role != null ? role.replace('_', ' ') : "STAFF"));
        binding.tvStaffEmail.setText(sessionManager.getEmail());

        String phone = sessionManager.getPhone();
        if (phone != null && !phone.trim().isEmpty()) {
            binding.tvStaffPhone.setText("Phone: " + phone);
            binding.tvStaffPhone.setVisibility(View.VISIBLE);
        } else {
            binding.tvStaffPhone.setVisibility(View.GONE);
        }

        String dept = sessionManager.getDepartmentName();
        binding.tvStaffDepartment.setText(dept != null && !dept.isEmpty() ? dept : "Hostel Administration");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
