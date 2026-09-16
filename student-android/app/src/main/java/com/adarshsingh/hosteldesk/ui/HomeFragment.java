package com.adarshsingh.hosteldesk.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.adarshsingh.hosteldesk.R;
import com.adarshsingh.hosteldesk.data.api.ApiClient;
import com.adarshsingh.hosteldesk.data.local.SessionManager;
import com.adarshsingh.hosteldesk.data.model.AnnouncementDto;
import com.adarshsingh.hosteldesk.data.model.StudentDashboardDto;
import com.adarshsingh.hosteldesk.databinding.FragmentHomeBinding;
import com.adarshsingh.hosteldesk.ui.adapter.IssueAdapter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private SessionManager sessionManager;
    private IssueAdapter issueAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = SessionManager.getInstance(requireContext());

        setupGreeting();
        setupRecyclerView();
        setupActionButtons();

        binding.swipeRefresh.setOnRefreshListener(this::loadDashboardData);
        loadDashboardData();
    }

    private void setupGreeting() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        String salutation = (hour < 12) ? "Good morning" : (hour < 17) ? "Good afternoon" : "Good evening";

        String name = sessionManager.getFullName();
        String firstName = (name != null && name.contains(" ")) ? name.split(" ")[0] : name;
        binding.tvGreeting.setText(salutation + ", " + (firstName != null && !firstName.isEmpty() ? firstName : "Resident"));

        String hostel = sessionManager.getHostelName();
        String room = sessionManager.getRoomNumber();
        String institute = sessionManager.getInstituteName();

        if (hostel != null && !hostel.isEmpty() && room != null && !room.isEmpty()) {
            binding.tvResidentSub.setText(hostel + " · " + (room.startsWith("Room") ? room : "Room " + room));
        } else if (hostel != null && !hostel.isEmpty()) {
            binding.tvResidentSub.setText(hostel);
        } else if (institute != null && !institute.isEmpty()) {
            binding.tvResidentSub.setText(institute);
        } else {
            binding.tvResidentSub.setText("Campus Residence");
        }

        String studentId = sessionManager.getStudentId();
        if (studentId != null && !studentId.isEmpty()) {
            binding.tvInstitutionalBadge.setText(studentId);
        } else {
            binding.tvInstitutionalBadge.setText("RESIDENT");
        }
    }

    private void setupRecyclerView() {
        issueAdapter = new IssueAdapter(issue -> {
            Intent intent = new Intent(requireContext(), IssueDetailActivity.class);
            intent.putExtra("issue_id", issue.getId());
            startActivity(intent);
        });
        binding.rvActiveIssues.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvActiveIssues.setAdapter(issueAdapter);
    }

    private void setupActionButtons() {
        binding.btnQuickDescribe.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), ReportIssueActivity.class);
            startActivity(intent);
        });

        binding.btnQuickPhoto.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), ReportIssueActivity.class);
            intent.putExtra("auto_launch_camera", true);
            startActivity(intent);
        });

        binding.btnQuickUrgent.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), ReportIssueActivity.class);
            intent.putExtra("priority_urgent", true);
            startActivity(intent);
        });

        binding.btnViewAllIssues.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).switchToTab(R.id.nav_issues);
            }
        });
    }

    private void loadDashboardData() {
        binding.swipeRefresh.setRefreshing(true);

        ApiClient.getApiService(requireContext()).getDashboard()
                .enqueue(new Callback<StudentDashboardDto>() {
                    @Override
                    public void onResponse(Call<StudentDashboardDto> call, Response<StudentDashboardDto> response) {
                        if (!isAdded()) return;
                        binding.swipeRefresh.setRefreshing(false);

                        if (response.code() == 401) {
                            sessionManager.clearSession();
                            Intent intent = new Intent(requireContext(), LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            return;
                        }

                        if (response.isSuccessful() && response.body() != null) {
                            renderDashboard(response.body());
                        } else {
                            Toast.makeText(requireContext(), "Failed to refresh dashboard (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<StudentDashboardDto> call, Throwable t) {
                        if (!isAdded()) return;
                        binding.swipeRefresh.setRefreshing(false);
                        Toast.makeText(requireContext(), "Connection offline or server unreachable", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void renderDashboard(StudentDashboardDto data) {
        if (data.getStudentName() != null && !data.getStudentName().trim().isEmpty()) {
            String firstName = data.getStudentName().contains(" ") ? data.getStudentName().split(" ")[0] : data.getStudentName();
            int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            String salutation = (hour < 12) ? "Good morning" : (hour < 17) ? "Good afternoon" : "Good evening";
            binding.tvGreeting.setText(salutation + ", " + firstName);
        }

        String location = "";
        if (data.getHostelName() != null) location += data.getHostelName();
        if (data.getBlockName() != null) location += ", " + data.getBlockName();
        if (data.getRoomNumber() != null) location += " · " + data.getRoomNumber();
        if (!location.isEmpty()) binding.tvResidentSub.setText(location);

        if (data.getInstitutionalId() != null && !data.getInstitutionalId().isEmpty()) {
            binding.tvInstitutionalBadge.setText(data.getInstitutionalId());
        }

        binding.tvWaterVital.setText(data.getWaterStatus() != null ? data.getWaterStatus() : "✓ Normal");
        binding.tvPowerVital.setText(data.getPowerStatus() != null ? data.getPowerStatus() : "✓ Normal");
        binding.tvRequestsVital.setText(data.getActiveRequestsCount() + " active");

        if (data.getMaintenanceNotice() != null && !data.getMaintenanceNotice().trim().isEmpty()) {
            binding.cardNoticeBanner.setVisibility(View.VISIBLE);
            binding.tvNoticeBanner.setText(data.getMaintenanceNotice());
        } else {
            binding.cardNoticeBanner.setVisibility(View.GONE);
        }

        binding.tvActiveIssuesCount.setText(data.getActiveRequestsCount() + " active");

        if (data.getActiveIssues() != null && !data.getActiveIssues().isEmpty()) {
            binding.rvActiveIssues.setVisibility(View.VISIBLE);
            binding.layoutEmptyState.setVisibility(View.GONE);
            issueAdapter.setItems(data.getActiveIssues());
        } else {
            binding.rvActiveIssues.setVisibility(View.GONE);
            binding.layoutEmptyState.setVisibility(View.VISIBLE);
        }

        loadAnnouncements();
    }

    private void loadAnnouncements() {
        ApiClient.getApiService(requireContext()).getAnnouncements()
                .enqueue(new Callback<List<AnnouncementDto>>() {
                    @Override
                    public void onResponse(Call<List<AnnouncementDto>> call, Response<List<AnnouncementDto>> response) {
                        if (!isAdded() || binding == null) return;
                        if (response.isSuccessful() && response.body() != null) {
                            renderAnnouncements(response.body());
                        } else {
                            renderAnnouncements(new ArrayList<>());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<AnnouncementDto>> call, Throwable t) {
                        if (!isAdded() || binding == null) return;
                        renderAnnouncements(new ArrayList<>());
                    }
                });
    }

    private void renderAnnouncements(List<AnnouncementDto> announcements) {
        binding.layoutAnnouncementsContainer.removeAllViews();
        if (announcements == null || announcements.isEmpty()) {
            binding.cardEmptyAnnouncements.setVisibility(View.VISIBLE);
            binding.tvAnnouncementsCount.setText("0 active");
            return;
        }

        binding.cardEmptyAnnouncements.setVisibility(View.GONE);
        binding.tvAnnouncementsCount.setText(announcements.size() + " active");

        float density = getResources().getDisplayMetrics().density;

        for (AnnouncementDto a : announcements) {
            com.google.android.material.card.MaterialCardView card = new com.google.android.material.card.MaterialCardView(requireContext());
            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            cardParams.setMargins(0, 0, 0, (int) (8 * density));
            card.setLayoutParams(cardParams);
            card.setCardBackgroundColor(getResources().getColor(R.color.colorCard));
            card.setRadius(12 * density);
            card.setStrokeColor(getResources().getColor(R.color.colorBorder));
            card.setStrokeWidth((int) (1 * density));
            card.setCardElevation(0);

            LinearLayout inner = new LinearLayout(requireContext());
            inner.setOrientation(LinearLayout.VERTICAL);
            inner.setPadding((int) (14 * density), (int) (14 * density), (int) (14 * density), (int) (14 * density));

            LinearLayout header = new LinearLayout(requireContext());
            header.setOrientation(LinearLayout.HORIZONTAL);
            header.setGravity(android.view.Gravity.CENTER_VERTICAL);

            android.widget.TextView tvTitle = new android.widget.TextView(requireContext());
            LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
            tvTitle.setLayoutParams(titleParams);
            tvTitle.setText(a.getTitle() != null ? a.getTitle() : "Notice");
            tvTitle.setTextColor(getResources().getColor(R.color.colorCharcoal));
            tvTitle.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 13);
            tvTitle.setTypeface(null, android.graphics.Typeface.BOLD);
            header.addView(tvTitle);

            if (Boolean.TRUE.equals(a.getPinned())) {
                android.widget.TextView tvPin = new android.widget.TextView(requireContext());
                tvPin.setText("PINNED");
                tvPin.setTextColor(getResources().getColor(R.color.colorPrimary));
                tvPin.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 10);
                tvPin.setTypeface(null, android.graphics.Typeface.BOLD);
                header.addView(tvPin);
            }
            inner.addView(header);

            android.widget.TextView tvContent = new android.widget.TextView(requireContext());
            LinearLayout.LayoutParams contentParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            contentParams.setMargins(0, (int) (4 * density), 0, 0);
            tvContent.setLayoutParams(contentParams);
            tvContent.setText(a.getContent() != null ? a.getContent() : "");
            tvContent.setTextColor(getResources().getColor(R.color.colorMutedText));
            tvContent.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 12);
            inner.addView(tvContent);

            android.widget.TextView tvFooter = new android.widget.TextView(requireContext());
            LinearLayout.LayoutParams footerParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            footerParams.setMargins(0, (int) (8 * density), 0, 0);
            tvFooter.setLayoutParams(footerParams);

            String authorInfo = (a.getAuthorRole() != null ? a.getAuthorRole().replace('_', ' ') : "Administration");
            if (a.getScope() != null) authorInfo += " · " + a.getScope().replace('_', ' ');
            if (a.getExpiresAt() != null) {
                authorInfo += " · Temporary Notice";
            }
            tvFooter.setText(authorInfo);
            tvFooter.setTextColor(0xFF8A99AD);
            tvFooter.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 11);
            inner.addView(tvFooter);

            card.addView(inner);
            binding.layoutAnnouncementsContainer.addView(card);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadDashboardData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
