package com.adarshsingh.hosteldesk.ui;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.adarshsingh.hosteldesk.R;
import com.adarshsingh.hosteldesk.databinding.ActivityAiPreviewBinding;

public class AiPreviewActivity extends AppCompatActivity {

    private ActivityAiPreviewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAiPreviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.setNavigationOnClickListener(v -> finish());

        long issueId = getIntent().getLongExtra("issue_id", -1L);
        String ticketNumber = getIntent().getStringExtra("ticket_number");
        String title = getIntent().getStringExtra("title");
        String department = getIntent().getStringExtra("department");
        String priority = getIntent().getStringExtra("priority");
        String aiSummary = getIntent().getStringExtra("ai_summary");
        String urgencyReason = getIntent().getStringExtra("urgency_reason");

        binding.tvTicketNumber.setText("Ticket #" + (ticketNumber != null ? ticketNumber : "HD-NEW"));
        binding.tvIssueTitle.setText(title != null ? title : "Hostel Issue Report");

        String displaySummary = "";
        if (aiSummary != null && !aiSummary.isEmpty()) {
            displaySummary = aiSummary;
        } else {
            displaySummary = "Issue successfully triaged. Routed to on-duty maintenance staff for expeditious handling.";
        }
        if (urgencyReason != null && !urgencyReason.isEmpty()) {
            displaySummary += "\n\n• Safety context: " + urgencyReason;
        }
        binding.tvSummary.setText(displaySummary);

        binding.tvDepartment.setText(department != null ? department : "General Maintenance");

        String prio = priority != null ? priority.toUpperCase() : "MEDIUM";
        binding.tvPriority.setText(prio);

        int prioBgColor;
        int prioTextColor;
        String slaText;

        switch (prio) {
            case "URGENT":
                prioBgColor = ContextCompat.getColor(this, R.color.colorUrgentLight);
                prioTextColor = ContextCompat.getColor(this, R.color.colorUrgent);
                slaText = "2 Hours";
                break;
            case "HIGH":
                prioBgColor = ContextCompat.getColor(this, R.color.colorAmberLight);
                prioTextColor = ContextCompat.getColor(this, R.color.colorAmberText);
                slaText = "4 Hours";
                break;
            default:
                prioBgColor = ContextCompat.getColor(this, R.color.colorSageLight);
                prioTextColor = ContextCompat.getColor(this, R.color.colorSage);
                slaText = "24 Hours";
                break;
        }

        binding.tvPriority.setBackgroundTintList(ColorStateList.valueOf(prioBgColor));
        binding.tvPriority.setTextColor(prioTextColor);
        binding.tvSla.setText(slaText);

        binding.btnDone.setOnClickListener(v -> {
            if (issueId != -1L) {
                Intent intent = new Intent(AiPreviewActivity.this, IssueDetailActivity.class);
                intent.putExtra("issue_id", issueId);
                startActivity(intent);
            }
            finish();
        });

        binding.btnReturnHome.setOnClickListener(v -> finish());
    }
}
