package com.adarshsingh.adminhosteldesk.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.adarshsingh.adminhosteldesk.data.model.ActivityDto;
import com.adarshsingh.adminhosteldesk.databinding.ItemActivityTimelineBinding;
import java.util.ArrayList;
import java.util.List;

public class ActivityTimelineAdapter extends RecyclerView.Adapter<ActivityTimelineAdapter.ViewHolder> {

    private final List<ActivityDto> items = new ArrayList<>();

    public void setItems(List<ActivityDto> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemActivityTimelineBinding binding = ItemActivityTimelineBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(items.get(position), position == items.size() - 1);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemActivityTimelineBinding binding;

        ViewHolder(ItemActivityTimelineBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(ActivityDto activity, boolean isLast) {
            binding.tvAction.setText(formatAction(activity.getAction(), activity));
            binding.tvTimestamp.setText(formatDate(activity.getCreatedAt()));

            String actorText = "";
            if (activity.getActorName() != null && !activity.getActorName().isEmpty()) {
                String name = activity.getActorName();
                String role = activity.getActorRole();
                if ("SYSTEM".equalsIgnoreCase(role) || "AUTOMATED".equalsIgnoreCase(role)) {
                    actorText = "Automated System";
                } else {
                    actorText = "By " + name;
                    if (role != null && !role.isEmpty()) {
                        actorText += " (" + role.replace('_', ' ') + ")";
                    }
                }
            } else {
                actorText = "Automated System";
            }
            binding.tvActor.setText(actorText);

            if (activity.getNotes() != null && !activity.getNotes().trim().isEmpty()) {
                binding.tvNotes.setVisibility(View.VISIBLE);
                binding.tvNotes.setText(activity.getNotes());
            } else {
                binding.tvNotes.setVisibility(View.GONE);
            }

            if (isLast) {
                binding.timelineLine.setVisibility(View.INVISIBLE);
            } else {
                binding.timelineLine.setVisibility(View.VISIBLE);
            }
        }

        private String formatAction(String action, ActivityDto activity) {
            if (action == null) return "Event";
            switch (action.toUpperCase()) {
                case "REPORTED":
                case "CREATED": return "Issue Reported";
                case "ANALYZED":
                case "AI_TRIAGED": return "AI Triage & Categorization";
                case "ROUTED":
                case "AUTO_ROUTED": return "Auto-Routed to Department";
                case "ASSIGNED":
                    if (activity != null && activity.getActorRole() != null) {
                        String role = activity.getActorRole().toUpperCase();
                        if (role.contains("INSTITUTE_ADMIN") || role.contains("SUPER_ADMIN") || (role.contains("ADMIN") && !role.contains("WARDEN"))) {
                            return "Routed by Institute Admin";
                        } else if (role.contains("WARDEN")) {
                            return "Routed by Warden";
                        }
                    }
                    return "Technician Assigned";
                case "IN_PROGRESS": return "Work Started";
                case "NOTE_ADDED":
                case "PROGRESS_UPDATED": return "Work Progress Update";
                case "COMPLETED":
                case "RESOLVED": return "Work Marked Completed";
                case "VERIFIED": return "Resident Verified Fixed";
                case "REOPENED": return "Issue Reopened by Resident";
                case "CANCELLED":
                case "CLOSED": return "Ticket Closed";
                default: return action.replace('_', ' ');
            }
        }

        private String formatDate(String isoDate) {
            if (isoDate == null || isoDate.isEmpty()) return "";
            try {
                if (isoDate.length() >= 16) {
                    String time = isoDate.substring(11, 16);
                    String date = isoDate.substring(0, 10);
                    return date + " · " + time;
                }
            } catch (Exception ignored) {}
            return isoDate;
        }
    }
}