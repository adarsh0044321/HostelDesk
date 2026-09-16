package com.adarshsingh.hosteldesk.ui;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.bumptech.glide.Glide;
import com.adarshsingh.hosteldesk.R;
import com.adarshsingh.hosteldesk.data.api.ApiClient;
import com.adarshsingh.hosteldesk.data.local.SessionManager;
import com.adarshsingh.hosteldesk.data.model.AttachmentDto;
import com.adarshsingh.hosteldesk.data.model.IssueDetailDto;
import com.adarshsingh.hosteldesk.data.model.ReopenIssueRequest;
import com.adarshsingh.hosteldesk.data.model.VerifyResolutionRequest;
import com.adarshsingh.hosteldesk.databinding.ActivityIssueDetailBinding;
import com.adarshsingh.hosteldesk.ui.adapter.ActivityTimelineAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IssueDetailActivity extends AppCompatActivity {

    private ActivityIssueDetailBinding binding;
    private ActivityTimelineAdapter timelineAdapter;
    private long issueId = -1L;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIssueDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = SessionManager.getInstance(this);
        issueId = getIntent().getLongExtra("issue_id", -1L);

        binding.toolbar.setNavigationOnClickListener(v -> finish());

        setupTimeline();

        binding.swipeRefreshDetail.setOnRefreshListener(this::fetchIssueDetail);

        if (issueId != -1L) {
            fetchIssueDetail();
        } else {
            Toast.makeText(this, "Issue ID not specified", Toast.LENGTH_SHORT).show();
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
                        if (response.isSuccessful() && response.body() != null) {
                            renderIssue(response.body());
                        } else {
                            Toast.makeText(IssueDetailActivity.this, "Failed to load issue details", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<IssueDetailDto> call, Throwable t) {
                        binding.swipeRefreshDetail.setRefreshing(false);
                        Toast.makeText(IssueDetailActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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

        // First attachment / student photo
        if (issue.getFirstAttachmentUrl() != null && !issue.getFirstAttachmentUrl().isEmpty()) {
            binding.ivInitialPhoto.setVisibility(View.VISIBLE);
            String fullUrl = resolveFileUrl(issue.getFirstAttachmentUrl());
            Glide.with(this).load(fullUrl).into(binding.ivInitialPhoto);
        } else {
            binding.ivInitialPhoto.setVisibility(View.GONE);
        }

        // Assigned Staff
        if (issue.getAssignedStaffName() != null && !issue.getAssignedStaffName().isEmpty()) {
            binding.cardAssignedStaff.setVisibility(View.VISIBLE);
            binding.tvStaffName.setText(issue.getAssignedStaffName());
            binding.tvStaffContact.setText("Assigned Technician · " + (issue.getAssignedDepartmentName() != null ? issue.getAssignedDepartmentName() : "Maintenance"));
            binding.tvStaffAvatar.setText(issue.getAssignedStaffName().substring(0, 1).toUpperCase());
        } else if (issue.getAssignedDepartmentName() != null) {
            binding.cardAssignedStaff.setVisibility(View.VISIBLE);
            binding.tvStaffName.setText(issue.getAssignedDepartmentName());
            binding.tvStaffContact.setText("Routed to Department · Awaiting Staff Assignment");
            binding.tvStaffAvatar.setText("D");
        } else {
            binding.cardAssignedStaff.setVisibility(View.GONE);
        }

        // Student Verification Section (Shown when AWAITING_VERIFICATION or RESOLVED)
        boolean isVerified = "VERIFIED".equalsIgnoreCase(status) || (issue.getVerifiedAt() != null && !"REOPENED".equalsIgnoreCase(status));
        if (isVerified) {
            binding.cardVerificationPrompt.setVisibility(View.GONE);
            binding.cardVerifiedStatus.setVisibility(View.VISIBLE);
            StringBuilder verifText = new StringBuilder("Resident confirmed work is complete.");
            if (issue.getRating() != null) {
                verifText.append(" Rated: ").append(issue.getRating()).append("★");
            }
            if (issue.getWorkerReview() != null && !issue.getWorkerReview().trim().isEmpty()) {
                verifText.append(" · \"").append(issue.getWorkerReview().trim()).append("\"");
            }
            binding.tvVerifiedDetails.setText(verifText.toString());
        } else if ("AWAITING_VERIFICATION".equalsIgnoreCase(status) || "RESOLVED".equalsIgnoreCase(status)) {
            binding.cardVerificationPrompt.setVisibility(View.VISIBLE);
            binding.cardVerifiedStatus.setVisibility(View.GONE);
            if (issue.getTechnicianNotes() != null && !issue.getTechnicianNotes().isEmpty()) {
                binding.tvResolutionSummary.setText("Technician Notes: \"" + issue.getTechnicianNotes() + "\"\n\nPlease inspect the repair and confirm if it has been resolved satisfactorily.");
            } else {
                binding.tvResolutionSummary.setText("Technician has reported this repair complete. Please inspect your room and verify.");
            }

            // Check if there is a resolution proof photo in attachments
            AttachmentDto proofAttachment = null;
            if (issue.getAttachments() != null) {
                for (AttachmentDto att : issue.getAttachments()) {
                    if ("RESOLUTION_PROOF".equalsIgnoreCase(att.getAttachmentType()) ||
                        "STAFF_COMPLETION_PROOF".equalsIgnoreCase(att.getAttachmentType())) {
                        proofAttachment = att;
                        break;
                    }
                }
            }

            if (proofAttachment != null && proofAttachment.getFileUrl() != null) {
                binding.ivResolutionProofPhoto.setVisibility(View.VISIBLE);
                String proofUrl = resolveFileUrl(proofAttachment.getFileUrl());
                Glide.with(this).load(proofUrl).into(binding.ivResolutionProofPhoto);
            } else {
                binding.ivResolutionProofPhoto.setVisibility(View.GONE);
            }

            binding.btnVerifyResolved.setOnClickListener(v -> confirmVerification());
            binding.btnReopenIssue.setOnClickListener(v -> promptReopenReason());
        } else {
            binding.cardVerificationPrompt.setVisibility(View.GONE);
            binding.cardVerifiedStatus.setVisibility(View.GONE);
        }

        // Timeline
        if (issue.getActivities() != null) {
            timelineAdapter.setItems(issue.getActivities());
        }
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

    private void confirmVerification() {
        android.widget.LinearLayout dialogLayout = new android.widget.LinearLayout(this);
        dialogLayout.setOrientation(android.widget.LinearLayout.VERTICAL);
        int pad = (int) (18 * getResources().getDisplayMetrics().density);
        dialogLayout.setPadding(pad, pad / 2, pad, pad / 2);

        android.widget.TextView tvPrompt = new android.widget.TextView(this);
        tvPrompt.setText("Please inspect the completed work and rate your experience with the assigned technician:");
        tvPrompt.setTextColor(0xFF334155);
        tvPrompt.setTextSize(14);
        dialogLayout.addView(tvPrompt);

        // Star rating bar container
        android.widget.LinearLayout starsLayout = new android.widget.LinearLayout(this);
        starsLayout.setOrientation(android.widget.LinearLayout.HORIZONTAL);
        starsLayout.setGravity(android.view.Gravity.CENTER_HORIZONTAL);
        starsLayout.setPadding(0, pad, 0, pad / 2);

        final int[] selectedRating = new int[]{ 5 }; // default 5 stars
        final android.widget.TextView[] starViews = new android.widget.TextView[5];

        for (int i = 0; i < 5; i++) {
            final int starIndex = i + 1;
            android.widget.TextView star = new android.widget.TextView(this);
            star.setText("★");
            star.setTextSize(34);
            star.setTextColor(0xFFF59E0B);
            star.setPadding(pad / 4, 0, pad / 4, 0);
            star.setClickable(true);
            star.setFocusable(true);
            star.setOnClickListener(v -> {
                selectedRating[0] = starIndex;
                for (int s = 0; s < 5; s++) {
                    starViews[s].setTextColor(s < starIndex ? 0xFFF59E0B : 0xFFCBD5E1);
                }
            });
            starViews[i] = star;
            starsLayout.addView(star);
        }
        dialogLayout.addView(starsLayout);

        final EditText etReview = new EditText(this);
        etReview.setHint("Share a review for the worker (optional, e.g. punctual, well done)...");
        etReview.setTextSize(13);
        etReview.setPadding(pad / 2, pad / 2, pad / 2, pad / 2);
        dialogLayout.addView(etReview);

        new AlertDialog.Builder(this)
                .setTitle("Confirm & Verify Work Done")
                .setView(dialogLayout)
                .setPositiveButton("Verify & Close Issue", (dialog, which) -> {
                    String reviewText = etReview.getText().toString().trim();
                    VerifyResolutionRequest request = new VerifyResolutionRequest(
                            "Verified fixed by student",
                            selectedRating[0],
                            reviewText.isEmpty() ? null : reviewText
                    );

                    ApiClient.getApiService(this).verifyResolution(issueId, request)
                            .enqueue(new Callback<IssueDetailDto>() {
                                @Override
                                public void onResponse(Call<IssueDetailDto> call, Response<IssueDetailDto> response) {
                                    if (response.isSuccessful() && response.body() != null) {
                                        Toast.makeText(IssueDetailActivity.this, "Thank you! Work marked verified and reviewed.", Toast.LENGTH_SHORT).show();
                                        renderIssue(response.body());
                                    } else {
                                        String errorMsg = "Failed to verify issue (" + response.code() + ")";
                                        try {
                                            if (response.errorBody() != null) {
                                                String raw = response.errorBody().string();
                                                org.json.JSONObject obj = new org.json.JSONObject(raw);
                                                if (obj.has("message")) {
                                                    errorMsg = obj.getString("message");
                                                } else if (obj.has("error")) {
                                                    errorMsg = obj.getString("error");
                                                }
                                            }
                                        } catch (Exception ignored) {}
                                        Toast.makeText(IssueDetailActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<IssueDetailDto> call, Throwable t) {
                                    Toast.makeText(IssueDetailActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }


    private void promptReopenReason() {
        final EditText input = new EditText(this);
        input.setHint("State why the repair was incomplete...");
        input.setPadding(32, 24, 32, 24);

        new AlertDialog.Builder(this)
                .setTitle("Reopen Issue")
                .setMessage("Please specify why the issue is not fixed:")
                .setView(input)
                .setPositiveButton("Reopen Ticket", (dialog, which) -> {
                    String reason = input.getText().toString().trim();
                    if (reason.isEmpty()) {
                        reason = "Issue persists after technician marked resolved";
                    }
                    ApiClient.getApiService(this).reopenIssue(issueId, new ReopenIssueRequest(reason))
                            .enqueue(new Callback<IssueDetailDto>() {
                                @Override
                                public void onResponse(Call<IssueDetailDto> call, Response<IssueDetailDto> response) {
                                    if (response.isSuccessful() && response.body() != null) {
                                        Toast.makeText(IssueDetailActivity.this, "Ticket reopened for follow-up inspection", Toast.LENGTH_SHORT).show();
                                        renderIssue(response.body());
                                    } else {
                                        Toast.makeText(IssueDetailActivity.this, "Failed to reopen ticket", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<IssueDetailDto> call, Throwable t) {
                                    Toast.makeText(IssueDetailActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
