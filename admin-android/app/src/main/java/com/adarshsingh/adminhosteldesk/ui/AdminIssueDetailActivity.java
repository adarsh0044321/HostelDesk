package com.adarshsingh.adminhosteldesk.ui;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.bumptech.glide.Glide;
import com.adarshsingh.adminhosteldesk.R;
import com.adarshsingh.adminhosteldesk.data.api.ApiClient;
import com.adarshsingh.adminhosteldesk.data.local.SessionManager;
import com.adarshsingh.adminhosteldesk.data.model.AttachmentDto;
import com.adarshsingh.adminhosteldesk.data.model.IssueDetailDto;
import com.adarshsingh.adminhosteldesk.databinding.ActivityAdminIssueDetailBinding;
import com.adarshsingh.adminhosteldesk.databinding.DialogStaffResolveBinding;
import com.adarshsingh.adminhosteldesk.ui.adapter.ActivityTimelineAdapter;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminIssueDetailActivity extends AppCompatActivity {

    private ActivityAdminIssueDetailBinding binding;
    private ActivityTimelineAdapter timelineAdapter;
    private long issueId = -1L;
    private SessionManager sessionManager;
    private IssueDetailDto currentIssue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminIssueDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = SessionManager.getInstance(this);
        issueId = getIntent().getLongExtra("issue_id", -1L);

        binding.toolbar.setNavigationOnClickListener(v -> finish());

        setupTimeline();

        binding.swipeRefreshDetail.setOnRefreshListener(this::fetchIssueDetail);

        if (issueId != -1L) {
            fetchIssueDetail();
        } else {
            Toast.makeText(this, "Ticket ID not specified", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupTimeline() {
        timelineAdapter = new ActivityTimelineAdapter();
        binding.rvTimeline.setLayoutManager(new LinearLayoutManager(this));
        binding.rvTimeline.setAdapter(timelineAdapter);
    }

    private void fetchIssueDetail() {
        binding.swipeRefreshDetail.setRefreshing(true);

        ApiClient.getApiService(this).getIssueDetail(issueId)
                .enqueue(new Callback<IssueDetailDto>() {
                    @Override
                    public void onResponse(Call<IssueDetailDto> call, Response<IssueDetailDto> response) {
                        binding.swipeRefreshDetail.setRefreshing(false);
                        if (response.code() == 401) {
                            handleSessionExpired();
                            return;
                        }
                        if (response.isSuccessful() && response.body() != null) {
                            currentIssue = response.body();
                            renderIssue(currentIssue);
                        } else {
                            Toast.makeText(AdminIssueDetailActivity.this, "Failed to load ticket details", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<IssueDetailDto> call, Throwable t) {
                        binding.swipeRefreshDetail.setRefreshing(false);
                        Toast.makeText(AdminIssueDetailActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void renderIssue(IssueDetailDto issue) {
        binding.tvTicketNumber.setText("TICKET #" + (issue.getTicketNumber() != null ? issue.getTicketNumber() : "HD-" + issue.getId()));
        binding.tvIssueTitle.setText(issue.getTitle());

        String hostel = (issue.getHostelName() != null && !issue.getHostelName().isEmpty()) ? issue.getHostelName() : sessionManager.getHostelName();
        String room = issue.getRoomNumber() != null ? issue.getRoomNumber() : "";
        String deptOrCat = (issue.getAssignedDepartmentName() != null && !issue.getAssignedDepartmentName().trim().isEmpty())
                ? issue.getAssignedDepartmentName()
                : (issue.getCategory() != null ? issue.getCategory() : "Maintenance");
        String meta = deptOrCat;
        if (hostel != null && !hostel.isEmpty()) {
            meta += " · " + hostel;
        }
        if (room != null && !room.isEmpty()) {
            meta += " " + (room.startsWith("Room") ? room : "Room " + room);
        }
        if (issue.getCreatedAt() != null && issue.getCreatedAt().length() >= 10) {
            meta += " · Reported " + issue.getCreatedAt().substring(0, 10);
        }
        binding.tvIssueMeta.setText(meta);
        binding.tvDescription.setText(issue.getDescription());

        // Priority Badge
        String priority = issue.getPriority() != null ? issue.getPriority() : "MEDIUM";
        binding.tvPriorityBadge.setText(priority);
        int prioBg = ContextCompat.getColor(this, "URGENT".equalsIgnoreCase(priority) ? R.color.colorUrgentLight : R.color.colorPrimaryContainer);
        int prioText = ContextCompat.getColor(this, "URGENT".equalsIgnoreCase(priority) ? R.color.colorUrgent : R.color.colorPrimary);
        binding.tvPriorityBadge.setBackgroundTintList(ColorStateList.valueOf(prioBg));
        binding.tvPriorityBadge.setTextColor(prioText);

        // Status Badge
        String status = issue.getStatus() != null ? issue.getStatus() : "SUBMITTED";
        binding.tvStatusBadge.setText(status.replace('_', ' '));
        applyStatusBadgeStyle(status);

        // Resident Info
        String resName = issue.getReportedByName() != null && !issue.getReportedByName().trim().isEmpty() ? issue.getReportedByName() : "Campus Student";
        binding.tvResidentName.setText("Resident: " + resName);
        StringBuilder locSb = new StringBuilder();
        if (issue.getRoomNumber() != null && !issue.getRoomNumber().trim().isEmpty()) {
            locSb.append("Room ").append(issue.getRoomNumber());
        }
        if (issue.getBlockName() != null && !issue.getBlockName().trim().isEmpty()) {
            if (locSb.length() > 0) locSb.append(" · ");
            locSb.append(issue.getBlockName());
        }
        if (issue.getHostelName() != null && !issue.getHostelName().trim().isEmpty()) {
            if (locSb.length() > 0) locSb.append(" · ");
            locSb.append(issue.getHostelName());
        }
        if (locSb.length() == 0) {
            locSb.append("Campus Residence");
        }
        binding.tvResidentContact.setText(locSb.toString());

        // Initial Photo Attached
        if (issue.getFirstAttachmentUrl() != null && !issue.getFirstAttachmentUrl().isEmpty()) {
            binding.ivInitialPhoto.setVisibility(View.VISIBLE);
            String fullUrl = resolveFileUrl(issue.getFirstAttachmentUrl());
            Glide.with(this).load(fullUrl).into(binding.ivInitialPhoto);
        } else {
            binding.ivInitialPhoto.setVisibility(View.GONE);
        }

        // Assigned Staff & Department
        if (issue.getAssignedStaffName() != null && !issue.getAssignedStaffName().isEmpty()) {
            binding.tvStaffName.setText("Technician: " + issue.getAssignedStaffName());
            binding.tvStaffContact.setText(issue.getAssignedDepartmentName() != null ? issue.getAssignedDepartmentName() : "Assigned Maintenance");
            binding.tvStaffAvatar.setText(issue.getAssignedStaffName().substring(0, 1).toUpperCase());
        } else if (issue.getAssignedDepartmentName() != null) {
            binding.tvStaffName.setText("Department: " + issue.getAssignedDepartmentName());
            binding.tvStaffContact.setText("Awaiting Individual Staff Dispatch");
            binding.tvStaffAvatar.setText("D");
        } else {
            binding.tvStaffName.setText("Unassigned");
            binding.tvStaffContact.setText("No department or technician assigned yet");
            binding.tvStaffAvatar.setText("?");
        }

        // Mode-specific actions (Staff vs Warden/Admin)
        boolean isStaff = "MAINTENANCE_STAFF".equalsIgnoreCase(sessionManager.getRole()) ||
                "STAFF".equalsIgnoreCase(sessionManager.getRole());

        if (isStaff) {
            binding.btnAssignOrReassign.setVisibility(View.GONE);
            binding.layoutStaffActions.setVisibility(View.VISIBLE);

            if ("ASSIGNED".equalsIgnoreCase(status) || "REOPENED".equalsIgnoreCase(status)) {
                binding.btnStartWork.setVisibility(View.VISIBLE);
                binding.btnStartWork.setOnClickListener(v -> startStaffWork());
                binding.btnCompleteWork.setVisibility(View.GONE);
            } else if ("IN_PROGRESS".equalsIgnoreCase(status)) {
                binding.btnStartWork.setVisibility(View.GONE);
                binding.btnCompleteWork.setVisibility(View.VISIBLE);
                binding.btnCompleteWork.setOnClickListener(v -> promptCompleteJob());
            } else {
                binding.layoutStaffActions.setVisibility(View.GONE);
            }
        } else {
            // Warden / Admin
            binding.layoutStaffActions.setVisibility(View.GONE);
            if ("AWAITING_VERIFICATION".equalsIgnoreCase(status) || "RESOLVED".equalsIgnoreCase(status) || "VERIFIED".equalsIgnoreCase(status) || "CLOSED".equalsIgnoreCase(status)) {
                binding.btnAssignOrReassign.setVisibility(View.GONE);
            } else {
                binding.btnAssignOrReassign.setVisibility(View.VISIBLE);
                if ("SUBMITTED".equalsIgnoreCase(status) || issue.getAssignedStaffName() == null) {
                    binding.btnAssignOrReassign.setText("Assign Technician");
                } else {
                    binding.btnAssignOrReassign.setText("Reassign Ticket");
                }
                binding.btnAssignOrReassign.setOnClickListener(v -> {
                    AssignDialogHelper.showAssignDialog(this, issue, this::fetchIssueDetail);
                });
            }
        }

        // Resolution Notes & Proof Photo
        boolean hasResolution = (issue.getResolutionNotes() != null && !issue.getResolutionNotes().trim().isEmpty());
        AttachmentDto proofAtt = null;
        if (issue.getAttachments() != null) {
            for (AttachmentDto att : issue.getAttachments()) {
                if ("RESOLUTION_PROOF".equalsIgnoreCase(att.getAttachmentType())) {
                    proofAtt = att;
                    break;
                }
            }
        }
        if (hasResolution || proofAtt != null) {
            binding.cardResolutionInfo.setVisibility(View.VISIBLE);
            binding.tvResolutionNotes.setText(hasResolution ? issue.getResolutionNotes() : "Repair completed by technician.");
            if (proofAtt != null && proofAtt.getFileUrl() != null) {
                binding.ivResolutionProof.setVisibility(View.VISIBLE);
                Glide.with(this).load(resolveFileUrl(proofAtt.getFileUrl())).into(binding.ivResolutionProof);
            } else {
                binding.ivResolutionProof.setVisibility(View.GONE);
            }
        } else {
            binding.cardResolutionInfo.setVisibility(View.GONE);
        }

        // Reopened Alert
        boolean isReopened = "REOPENED".equalsIgnoreCase(status);
        if (isReopened) {
            binding.cardReopenAlert.setVisibility(View.VISIBLE);
            if (issue.getReopenReason() != null && !issue.getReopenReason().trim().isEmpty()) {
                binding.tvReopenReason.setText("Resident Reopen Reason: " + issue.getReopenReason());
            } else {
                binding.tvReopenReason.setText("Resident reported work is incomplete or unsatisfactory. Ticket requires reassignment or further action.");
            }
        } else {
            binding.cardReopenAlert.setVisibility(View.GONE);
        }

        // Student Feedback & Worker Review
        if (issue.getRating() != null || (issue.getWorkerReview() != null && !issue.getWorkerReview().trim().isEmpty())) {
            binding.cardStudentFeedback.setVisibility(View.VISIBLE);
            int ratingVal = issue.getRating() != null ? issue.getRating() : 5;
            StringBuilder stars = new StringBuilder();
            for (int i = 0; i < 5; i++) {
                stars.append(i < ratingVal ? "★" : "☆");
            }
            stars.append(" (").append(ratingVal).append("/5)");
            binding.tvResidentRatingStars.setText(stars.toString());

            if (issue.getWorkerReview() != null && !issue.getWorkerReview().trim().isEmpty()) {
                binding.tvResidentReviewText.setText("\"" + issue.getWorkerReview() + "\"");
                binding.tvResidentReviewText.setVisibility(View.VISIBLE);
            } else {
                binding.tvResidentReviewText.setText("Resident verified work completed satisfactorily.");
                binding.tvResidentReviewText.setVisibility(View.VISIBLE);
            }
        } else {
            binding.cardStudentFeedback.setVisibility(View.GONE);
        }

        // Timeline
        if (issue.getActivities() != null) {
            timelineAdapter.setItems(issue.getActivities());
        }
    }

    private void startStaffWork() {
        ApiClient.getApiService(this).startWork(issueId)
                .enqueue(new Callback<IssueDetailDto>() {
                    @Override
                    public void onResponse(Call<IssueDetailDto> call, Response<IssueDetailDto> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Toast.makeText(AdminIssueDetailActivity.this, "Work started on ticket", Toast.LENGTH_SHORT).show();
                            renderIssue(response.body());
                        } else {
                            Toast.makeText(AdminIssueDetailActivity.this, "Failed to start work", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<IssueDetailDto> call, Throwable t) {
                        Toast.makeText(AdminIssueDetailActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void promptCompleteJob() {
        DialogStaffResolveBinding dialogBinding = DialogStaffResolveBinding.inflate(LayoutInflater.from(this));
        dialogBinding.tvResolveTicketInfo.setText("Ticket #" + (currentIssue.getTicketNumber() != null ? currentIssue.getTicketNumber() : "HD-" + currentIssue.getId()) +
                " · " + currentIssue.getTitle());

        new AlertDialog.Builder(this)
                .setView(dialogBinding.getRoot())
                .setPositiveButton("Submit & Mark Fixed", (dialog, which) -> {
                    String notes = dialogBinding.etResolveNotes.getText() != null ? dialogBinding.etResolveNotes.getText().toString().trim() : "";
                    if (notes.isEmpty()) {
                        notes = "Maintenance repair completed successfully by technician.";
                    }

                    RequestBody notesPart = RequestBody.create(MediaType.parse("text/plain"), notes);

                    ApiClient.getApiService(this).completeWork(issueId, notesPart, null)
                            .enqueue(new Callback<IssueDetailDto>() {
                                @Override
                                public void onResponse(Call<IssueDetailDto> call, Response<IssueDetailDto> response) {
                                    if (response.isSuccessful() && response.body() != null) {
                                        Toast.makeText(AdminIssueDetailActivity.this, "Ticket marked fixed! Resident notified.", Toast.LENGTH_LONG).show();
                                        renderIssue(response.body());
                                    } else {
                                        Toast.makeText(AdminIssueDetailActivity.this, "Failed to complete ticket", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<IssueDetailDto> call, Throwable t) {
                                    Toast.makeText(AdminIssueDetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void applyStatusBadgeStyle(String status) {
        int bg;
        int text;
        switch (status.toUpperCase()) {
            case "RESOLVED":
                bg = ContextCompat.getColor(this, R.color.colorAmberLight);
                text = ContextCompat.getColor(this, R.color.colorAmberText);
                break;
            case "VERIFIED":
            case "CLOSED":
                bg = ContextCompat.getColor(this, R.color.colorSageLight);
                text = ContextCompat.getColor(this, R.color.colorSage);
                break;
            case "IN_PROGRESS":
                bg = ContextCompat.getColor(this, R.color.colorProgressLight);
                text = ContextCompat.getColor(this, R.color.colorProgress);
                break;
            case "REOPENED":
                bg = ContextCompat.getColor(this, R.color.colorUrgentLight);
                text = ContextCompat.getColor(this, R.color.colorUrgent);
                break;
            default:
                bg = ContextCompat.getColor(this, R.color.colorBorderSubtle);
                text = ContextCompat.getColor(this, R.color.colorCharcoal);
                break;
        }
        binding.tvStatusBadge.setBackgroundTintList(ColorStateList.valueOf(bg));
        binding.tvStatusBadge.setTextColor(text);
    }

    private String resolveFileUrl(String relativeUrl) {
        if (relativeUrl.startsWith("http://") || relativeUrl.startsWith("https://")) {
            return relativeUrl;
        }
        String baseUrl = sessionManager.getBaseUrl();
        if (baseUrl.endsWith("/api/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 5);
        } else if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }
        return baseUrl + (relativeUrl.startsWith("/") ? "" : "/") + relativeUrl;
    }

    private void handleSessionExpired() {
        Toast.makeText(this, "Session expired. Please log in again.", Toast.LENGTH_LONG).show();
        sessionManager.clearSession();
        Intent intent = new Intent(this, AdminLoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}