package com.adarshsingh.adminhosteldesk.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.adarshsingh.adminhosteldesk.R;
import com.adarshsingh.adminhosteldesk.data.api.ApiClient;
import com.adarshsingh.adminhosteldesk.data.local.SessionManager;
import com.adarshsingh.adminhosteldesk.data.model.IssueDto;
import com.adarshsingh.adminhosteldesk.data.model.WardenDashboardDto;
import com.adarshsingh.adminhosteldesk.databinding.FragmentWardenOverviewBinding;
import com.adarshsingh.adminhosteldesk.ui.adapter.AdminTicketAdapter;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WardenOverviewFragment extends Fragment {

    private FragmentWardenOverviewBinding binding;
    private SessionManager sessionManager;
    private AdminTicketAdapter attentionAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentWardenOverviewBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = SessionManager.getInstance(requireContext());

        setupHeader();
        setupRecyclerView();

        binding.swipeRefreshOverview.setOnRefreshListener(this::fetchDashboardData);

        binding.btnViewCluster.setOnClickListener(v -> {
            if (getActivity() instanceof AdminMainActivity) {
                ((AdminMainActivity) getActivity()).switchToTab(R.id.nav_insights);
            }
        });

        binding.btnViewAllTickets.setOnClickListener(v -> {
            if (getActivity() instanceof AdminMainActivity) {
                ((AdminMainActivity) getActivity()).switchToTab(R.id.nav_tickets);
            }
        });

        binding.btnViewCrewDirectory.setOnClickListener(v -> {
            WorkersDirectoryBottomSheet sheet = WorkersDirectoryBottomSheet.newInstance("ALL");
            sheet.show(getChildFragmentManager(), "WorkersDirectoryBottomSheet");
        });

        fetchDashboardData();
    }

    private void setupHeader() {
        String role = sessionManager.getRole();
        boolean isInstAdmin = "INSTITUTE_ADMIN".equalsIgnoreCase(role) || "ADMIN".equalsIgnoreCase(role) || "SUPER_ADMIN".equalsIgnoreCase(role);
        String name = sessionManager.getFullName();
        String title = isInstAdmin ? "Institute Console" : "Warden Desk";
        if (name != null && !name.isEmpty()) {
            title = isInstAdmin ? "Console · " + (name.contains(" ") ? name.split(" ")[0] : name) :
                    (name.contains(" ") ? "Desk · " + name.split(" ")[0] : "Desk · " + name);
        }
        binding.tvWardenWelcome.setText(title);

        String hostel = sessionManager.getHostelName();
        String inst = sessionManager.getInstituteName();
        String campus = isInstAdmin ?
                ((inst != null && !inst.trim().isEmpty()) ? inst : "Campus Operations") :
                ((hostel != null && !hostel.trim().isEmpty()) ? hostel :
                ((inst != null && !inst.trim().isEmpty()) ? inst : "Campus Residence"));
        binding.tvHostelSubtitle.setText(campus + (isInstAdmin ? " · Institutional Deck" : " · Facilities Hub"));
    }

    private void setupRecyclerView() {
        attentionAdapter = new AdminTicketAdapter(new AdminTicketAdapter.OnTicketActionListener() {
            @Override
            public void onTicketClick(IssueDto ticket) {
                if (ticket != null && ticket.getId() != null) {
                    android.content.Intent intent = new android.content.Intent(requireContext(), AdminIssueDetailActivity.class);
                    intent.putExtra("issue_id", ticket.getId());
                    startActivity(intent);
                }
            }

            @Override
            public void onAssignClick(IssueDto ticket) {
                AssignDialogHelper.showAssignDialog(requireContext(), ticket, () -> fetchDashboardData());
            }

            @Override
            public void onWorkActionClick(IssueDto ticket) {}
        }, false);

        binding.rvAttentionTickets.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvAttentionTickets.setAdapter(attentionAdapter);
    }

    private void fetchDashboardData() {
        binding.swipeRefreshOverview.setRefreshing(true);

        ApiClient.getApiService(requireContext()).getWardenDashboard()
                .enqueue(new Callback<WardenDashboardDto>() {
                    @Override
                    public void onResponse(Call<WardenDashboardDto> call, Response<WardenDashboardDto> response) {
                        if (!isAdded()) return;
                        binding.swipeRefreshOverview.setRefreshing(false);

                        if (response.code() == 401) {
                            handleSessionExpired();
                            return;
                        }

                        if (response.isSuccessful() && response.body() != null) {
                            renderDashboard(response.body());
                        } else {
                            Toast.makeText(requireContext(), "Failed to load dashboard metrics", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<WardenDashboardDto> call, Throwable t) {
                        if (!isAdded()) return;
                        binding.swipeRefreshOverview.setRefreshing(false);
                    }
                });
    }

    private void handleSessionExpired() {
        if (!isAdded()) return;
        Toast.makeText(requireContext(), "Session expired. Please log in again.", Toast.LENGTH_SHORT).show();
        sessionManager.clearSession();
        android.content.Intent intent = new android.content.Intent(requireContext(), AdminLoginActivity.class);
        intent.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK | android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        if (getActivity() != null) getActivity().finish();
    }

    private void renderDashboard(WardenDashboardDto data) {
        binding.tvOpenCount.setText(String.valueOf(data.getTotalOpenCount()));
        if (data.getUrgentP1Count() > 0) {
            binding.tvUrgentCount.setVisibility(View.VISIBLE);
            binding.tvUrgentCount.setText(data.getUrgentP1Count() + " Critical");
        } else {
            binding.tvUrgentCount.setVisibility(View.GONE);
        }
        binding.tvInProgressCount.setText(String.valueOf(data.getInWorkCount()));
        binding.tvResolutionRate.setText(data.getHealthPercentage() + "%");

        if (data.getHealthStatus() != null) {
            binding.tvLivePulse.setText("● " + data.getHealthStatus().toUpperCase());
        }

        // Warden Audit & Work Review Metrics
        binding.tvAuditViewsBadge.setText(data.getTotalWardenViews() + (data.getTotalWardenViews() == 1 ? " view" : " views"));
        binding.tvAuditReviewedIssues.setText(data.getViewedIssuesCount() + " Reviewed");
        binding.tvAuditUnreviewedIssues.setText(data.getUnviewedIssuesCount() + " Unreviewed");
        binding.tvAuditStaffAssigned.setText(data.getAssignedStaffTasksCount() + " Assigned");
        binding.tvAuditStaffUnassigned.setText(data.getUnassignedStaffTasksCount() + " Needs Staff");
        binding.tvAuditTotalStaff.setText(data.getTotalHostelStaffCount() + " Staff");

        if (data.getRecurringInsights() != null && !data.getRecurringInsights().isEmpty()) {
            binding.cardClusterAlert.setVisibility(View.VISIBLE);
            binding.tvClusterMessage.setText(data.getRecurringInsights().get(0).getPatternDescription() +
                    "\n• Probable cause: " + data.getRecurringInsights().get(0).getProbableCause());
        } else {
            binding.cardClusterAlert.setVisibility(View.GONE);
        }

        List<IssueDto> attention = data.getAttentionRequired();
        if (attention != null && !attention.isEmpty()) {
            binding.rvAttentionTickets.setVisibility(View.VISIBLE);
            binding.tvNoAttentionNeeded.setVisibility(View.GONE);
            attentionAdapter.setItems(attention);
        } else {
            binding.rvAttentionTickets.setVisibility(View.GONE);
            binding.tvNoAttentionNeeded.setVisibility(View.VISIBLE);
        }

        // Render dynamic department workload matrix
        binding.layoutDepartmentMatrix.removeAllViews();
        List<java.util.Map<String, Object>> workloads = data.getDepartmentWorkloads();
        if (workloads == null || workloads.isEmpty()) {
            binding.tvNoDepartments.setVisibility(View.VISIBLE);
        } else {
            binding.tvNoDepartments.setVisibility(View.GONE);
            for (int i = 0; i < workloads.size(); i++) {
                java.util.Map<String, Object> map = workloads.get(i);
                String deptName = map.containsKey("displayName") ? String.valueOf(map.get("displayName")) : String.valueOf(map.get("name"));
                long staff = map.containsKey("staffCount") ? ((Number) map.get("staffCount")).longValue() :
                        (map.containsKey("staffOnDuty") ? ((Number) map.get("staffOnDuty")).longValue() : 0);
                long active = map.containsKey("activeTasks") ? ((Number) map.get("activeTasks")).longValue() : 0;

                android.widget.LinearLayout row = new android.widget.LinearLayout(requireContext());
                row.setOrientation(android.widget.LinearLayout.HORIZONTAL);
                row.setGravity(android.view.Gravity.CENTER_VERTICAL);
                row.setLayoutParams(new android.widget.LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                row.setPadding(dp(4), dp(6), dp(4), dp(6));
                row.setClickable(true);
                row.setFocusable(true);

                final String targetDept = deptName;
                row.setOnClickListener(v -> {
                    WorkersDirectoryBottomSheet sheet = WorkersDirectoryBottomSheet.newInstance(targetDept);
                    sheet.show(getChildFragmentManager(), "WorkersDirectoryBottomSheet");
                });

                android.widget.TextView tvName = new android.widget.TextView(requireContext());
                tvName.setLayoutParams(new android.widget.LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
                tvName.setText(deptName);
                tvName.setTextSize(14);
                tvName.setTextColor(0xFF1E293B);
                tvName.setTypeface(null, android.graphics.Typeface.BOLD);
                row.addView(tvName);

                android.widget.TextView tvStatus = new android.widget.TextView(requireContext());
                tvStatus.setText(staff + " staff · " + active + " active  ›");
                tvStatus.setTextSize(12);
                tvStatus.setTextColor(active > 0 ? 0xFFDC2626 : 0xFF10B981);
                tvStatus.setTypeface(null, android.graphics.Typeface.BOLD);
                row.addView(tvStatus);

                binding.layoutDepartmentMatrix.addView(row);

                if (i < workloads.size() - 1) {
                    View divider = new View(requireContext());
                    android.widget.LinearLayout.LayoutParams divParams = new android.widget.LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, dp(1));
                    divParams.setMargins(0, dp(8), 0, dp(8));
                    divider.setLayoutParams(divParams);
                    divider.setBackgroundColor(0xFFE2E8F0);
                    binding.layoutDepartmentMatrix.addView(divider);
                }
            }
        }
    }

    private int dp(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density + 0.5f);
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchDashboardData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
