package com.adarshsingh.adminhosteldesk.ui.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.adarshsingh.adminhosteldesk.R;
import com.adarshsingh.adminhosteldesk.data.model.IssueDto;
import com.adarshsingh.adminhosteldesk.databinding.ItemAdminTicketCardBinding;
import java.util.ArrayList;
import java.util.List;

public class AdminTicketAdapter extends RecyclerView.Adapter<AdminTicketAdapter.ViewHolder> {

    public interface OnTicketActionListener {
        void onTicketClick(IssueDto ticket);
        void onAssignClick(IssueDto ticket);
        void onWorkActionClick(IssueDto ticket);
    }

    private final List<IssueDto> items = new ArrayList<>();
    private final OnTicketActionListener listener;
    private final boolean isStaffMode;

    public AdminTicketAdapter(OnTicketActionListener listener, boolean isStaffMode) {
        this.listener = listener;
        this.isStaffMode = isStaffMode;
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
        ItemAdminTicketCardBinding binding = ItemAdminTicketCardBinding.inflate(
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
        private final ItemAdminTicketCardBinding binding;

        ViewHolder(ItemAdminTicketCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(IssueDto ticket) {
            Context context = binding.getRoot().getContext();

            binding.tvTicketNumber.setText(ticket.getTicketNumber() != null ? "#" + ticket.getTicketNumber() : "#HD-" + ticket.getId());
            binding.tvTitle.setText(ticket.getTitle());

            String hostel = ticket.getHostelName();
            StringBuilder locBuilder = new StringBuilder();
            if (hostel != null && !hostel.trim().isEmpty()) {
                locBuilder.append(hostel);
            }
            if (ticket.getBlockName() != null && !ticket.getBlockName().trim().isEmpty()) {
                if (locBuilder.length() > 0) locBuilder.append(" · ");
                locBuilder.append(ticket.getBlockName());
            }
            if (ticket.getRoomNumber() != null && !ticket.getRoomNumber().trim().isEmpty()) {
                if (locBuilder.length() > 0) locBuilder.append(" · ");
                locBuilder.append("Room ").append(ticket.getRoomNumber());
            }
            if (locBuilder.length() == 0) {
                locBuilder.append("Campus Residence");
            }
            binding.tvLocation.setText(locBuilder.toString());

            if (ticket.getReportedByName() != null && !ticket.getReportedByName().trim().isEmpty()) {
                binding.tvStudentName.setText("Resident: " + ticket.getReportedByName());
            } else {
                binding.tvStudentName.setText("Resident: Campus Student");
            }

            // Priority
            String priority = ticket.getPriority() != null ? ticket.getPriority() : "MEDIUM";
            binding.tvPriorityBadge.setText(priority);
            int prioBg = ContextCompat.getColor(context, "URGENT".equalsIgnoreCase(priority) ? R.color.colorUrgentLight : R.color.colorPrimaryContainer);
            int prioText = ContextCompat.getColor(context, "URGENT".equalsIgnoreCase(priority) ? R.color.colorUrgent : R.color.colorPrimary);
            binding.tvPriorityBadge.setBackgroundTintList(ColorStateList.valueOf(prioBg));
            binding.tvPriorityBadge.setTextColor(prioText);

            // Status
            String status = ticket.getStatus() != null ? ticket.getStatus() : "SUBMITTED";
            binding.tvStatusBadge.setText(status.replace('_', ' '));
            applyStatusStyle(context, status);

            // Staff / Department
            if (ticket.getAssignedStaffName() != null && !ticket.getAssignedStaffName().isEmpty()) {
                String deptPart = (ticket.getAssignedDepartmentName() != null) ? " (" + ticket.getAssignedDepartmentName() + ")" : "";
                binding.tvStaffAssigned.setText("Tech: " + ticket.getAssignedStaffName() + deptPart);
            } else if (ticket.getAssignedDepartmentName() != null && !ticket.getAssignedDepartmentName().isEmpty()) {
                binding.tvStaffAssigned.setText("Dept: " + ticket.getAssignedDepartmentName());
            } else if (ticket.getCategory() != null && !ticket.getCategory().isEmpty()) {
                binding.tvStaffAssigned.setText("Dept: " + ticket.getCategory());
            } else {
                binding.tvStaffAssigned.setText("⚠️ Needs Assignment");
            }

            // Buttons according to mode
            if (isStaffMode) {
                binding.btnAssignStaff.setVisibility(View.GONE);
                binding.btnActionWork.setVisibility(View.VISIBLE);

                if ("ASSIGNED".equalsIgnoreCase(status)) {
                    binding.btnActionWork.setText("Start Work");
                    binding.btnActionWork.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorProgress)));
                } else if ("IN_PROGRESS".equalsIgnoreCase(status)) {
                    binding.btnActionWork.setText("Mark Fixed");
                    binding.btnActionWork.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorSage)));
                } else {
                    binding.btnActionWork.setVisibility(View.GONE);
                }
            } else {
                // Warden / Admin mode
                binding.btnActionWork.setVisibility(View.GONE);

                boolean isVerificationOrResolved = "AWAITING_VERIFICATION".equalsIgnoreCase(status)
                        || "RESOLVED".equalsIgnoreCase(status)
                        || "VERIFIED".equalsIgnoreCase(status)
                        || "CLOSED".equalsIgnoreCase(status)
                        || "CANCELLED".equalsIgnoreCase(status);

                if (isVerificationOrResolved) {
                    // Hide Reassign button during verification and resolved phases
                    binding.btnAssignStaff.setVisibility(View.GONE);
                } else if ("SUBMITTED".equalsIgnoreCase(status) || ticket.getAssignedStaffName() == null) {
                    binding.btnAssignStaff.setVisibility(View.VISIBLE);
                    binding.btnAssignStaff.setText("Assign Staff");
                } else {
                    // Active work or REOPENED (verification rejected / unsatisfactory)
                    binding.btnAssignStaff.setVisibility(View.VISIBLE);
                    binding.btnAssignStaff.setText("Reassign");
                }
            }

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) listener.onTicketClick(ticket);
            });

            binding.btnAssignStaff.setOnClickListener(v -> {
                if (listener != null) listener.onAssignClick(ticket);
            });

            binding.btnActionWork.setOnClickListener(v -> {
                if (listener != null) listener.onWorkActionClick(ticket);
            });
        }

        private void applyStatusStyle(Context context, String status) {
            int bg;
            int text;
            switch (status.toUpperCase()) {
                case "RESOLVED":
                    bg = ContextCompat.getColor(context, R.color.colorAmberLight);
                    text = ContextCompat.getColor(context, R.color.colorAmberText);
                    break;
                case "VERIFIED":
                case "CLOSED":
                    bg = ContextCompat.getColor(context, R.color.colorSageLight);
                    text = ContextCompat.getColor(context, R.color.colorSage);
                    break;
                case "IN_PROGRESS":
                    bg = ContextCompat.getColor(context, R.color.colorProgressLight);
                    text = ContextCompat.getColor(context, R.color.colorProgress);
                    break;
                case "REOPENED":
                    bg = ContextCompat.getColor(context, R.color.colorUrgentLight);
                    text = ContextCompat.getColor(context, R.color.colorUrgent);
                    break;
                default:
                    bg = ContextCompat.getColor(context, R.color.colorBorderSubtle);
                    text = ContextCompat.getColor(context, R.color.colorCharcoal);
                    break;
            }
            binding.tvStatusBadge.setBackgroundTintList(ColorStateList.valueOf(bg));
            binding.tvStatusBadge.setTextColor(text);
        }
    }
}
