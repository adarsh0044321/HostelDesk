package com.adarshsingh.hosteldesk.ui.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.adarshsingh.hosteldesk.R;
import com.adarshsingh.hosteldesk.data.model.IssueDto;
import com.adarshsingh.hosteldesk.databinding.ItemIssueCardBinding;
import java.util.ArrayList;
import java.util.List;

public class IssueAdapter extends RecyclerView.Adapter<IssueAdapter.ViewHolder> {

    public interface OnIssueClickListener {
        void onIssueClick(IssueDto issue);
    }

    private final List<IssueDto> items = new ArrayList<>();
    private final OnIssueClickListener listener;

    public IssueAdapter(OnIssueClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<IssueDto> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemIssueCardBinding binding = ItemIssueCardBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemIssueCardBinding binding;

        ViewHolder(ItemIssueCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(IssueDto issue) {
            Context context = binding.getRoot().getContext();

            binding.tvTicketNumber.setText(issue.getTicketNumber() != null ? "#" + issue.getTicketNumber() : "#HD-0000");
            binding.tvCategory.setText(issue.getCategory() != null ? issue.getCategory() : "General");
            binding.tvTitle.setText(issue.getTitle());

            String location = "";
            if (issue.getBlockName() != null && !issue.getBlockName().isEmpty()) {
                location += issue.getBlockName();
            }
            if (issue.getRoomNumber() != null && !issue.getRoomNumber().isEmpty()) {
                if (!location.isEmpty()) location += " · ";
                location += "Room " + issue.getRoomNumber();
            }
            if (location.isEmpty()) {
                location = issue.getHostelName() != null ? issue.getHostelName() : "Campus Hostel";
            }
            binding.tvLocation.setText(location);

            // Status Styling
            String status = issue.getStatus() != null ? issue.getStatus() : "SUBMITTED";
            binding.tvStatus.setText(formatStatus(status));
            applyStatusColor(context, status);

            // Technician / Staff Info
            if (issue.getAssignedStaffName() != null && !issue.getAssignedStaffName().isEmpty()) {
                binding.tvTechnician.setText("Assigned: " + issue.getAssignedStaffName());
            } else if (issue.getAssignedDepartmentName() != null && !issue.getAssignedDepartmentName().isEmpty()) {
                binding.tvTechnician.setText("Routed: " + issue.getAssignedDepartmentName());
            } else {
                binding.tvTechnician.setText("Pending assignment");
            }

            // Time / Date
            binding.tvTimeAgo.setText(formatDate(issue.getCreatedAt()));

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    listener.onIssueClick(issue);
                }
            });
        }

        private String formatStatus(String status) {
            switch (status.toUpperCase()) {
                case "SUBMITTED": return "Submitted";
                case "ASSIGNED": return "Assigned";
                case "IN_PROGRESS": return "In Progress";
                case "RESOLVED": return "Needs Verification";
                case "VERIFIED": return "Verified Fixed";
                case "CLOSED": return "Closed";
                case "REOPENED": return "Reopened";
                case "CANCELLED": return "Cancelled";
                default: return status;
            }
        }

        private void applyStatusColor(Context context, String status) {
            int bgColor;
            int textColor;

            switch (status.toUpperCase()) {
                case "RESOLVED":
                    bgColor = ContextCompat.getColor(context, R.color.colorAmberLight);
                    textColor = ContextCompat.getColor(context, R.color.colorAmberText);
                    break;
                case "VERIFIED":
                case "CLOSED":
                    bgColor = ContextCompat.getColor(context, R.color.colorSageLight);
                    textColor = ContextCompat.getColor(context, R.color.colorSage);
                    break;
                case "IN_PROGRESS":
                    bgColor = ContextCompat.getColor(context, R.color.colorProgressLight);
                    textColor = ContextCompat.getColor(context, R.color.colorProgress);
                    break;
                case "REOPENED":
                    bgColor = ContextCompat.getColor(context, R.color.colorUrgentLight);
                    textColor = ContextCompat.getColor(context, R.color.colorUrgent);
                    break;
                default:
                    bgColor = ContextCompat.getColor(context, R.color.colorPrimaryContainer);
                    textColor = ContextCompat.getColor(context, R.color.colorPrimary);
                    break;
            }

            binding.tvStatus.setBackgroundTintList(ColorStateList.valueOf(bgColor));
            binding.tvStatus.setTextColor(textColor);
        }

        private String formatDate(String isoDate) {
            if (isoDate == null || isoDate.isEmpty()) return "Recently";
            try {
                // Return simplified formatted time or substring
                if (isoDate.length() >= 16) {
                    return isoDate.substring(0, 10) + " " + isoDate.substring(11, 16);
                }
                return isoDate;
            } catch (Exception e) {
                return "Recently";
            }
        }
    }
}
