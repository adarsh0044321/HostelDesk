package com.adarshsingh.adminhosteldesk.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.adarshsingh.adminhosteldesk.data.model.CrewWorkloadDto;
import com.adarshsingh.adminhosteldesk.data.model.HostelDto;
import com.adarshsingh.adminhosteldesk.data.model.UserDto;
import com.adarshsingh.adminhosteldesk.databinding.ItemDirectoryCardBinding;
import java.util.ArrayList;
import java.util.List;

public class DirectoryAdapter extends RecyclerView.Adapter<DirectoryAdapter.ViewHolder> {

    public enum Mode { HOSTELS, CREWS, STUDENTS, STAFF }

    public interface OnHostelActionListener {
        void onAssignWarden(HostelDto hostel);
        void onEditHostel(HostelDto hostel);
        void onDeleteHostel(HostelDto hostel);
    }

    private Mode currentMode = Mode.HOSTELS;
    private final List<Object> items = new ArrayList<>();
    private OnHostelActionListener hostelActionListener;

    public void setOnHostelActionListener(OnHostelActionListener listener) {
        this.hostelActionListener = listener;
    }

    public void setHostels(List<HostelDto> hostels) {
        currentMode = Mode.HOSTELS;
        items.clear();
        if (hostels != null) items.addAll(hostels);
        notifyDataSetChanged();
    }

    public void setCrews(List<CrewWorkloadDto> crews) {
        currentMode = Mode.CREWS;
        items.clear();
        if (crews != null) items.addAll(crews);
        notifyDataSetChanged();
    }

    public void setStudents(List<UserDto> students) {
        currentMode = Mode.STUDENTS;
        items.clear();
        if (students != null) items.addAll(students);
        notifyDataSetChanged();
    }

    public void setStaff(List<UserDto> staffList) {
        currentMode = Mode.STAFF;
        items.clear();
        if (staffList != null) items.addAll(staffList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDirectoryCardBinding binding = ItemDirectoryCardBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Object item = items.get(position);
        holder.bind(item, currentMode, hostelActionListener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemDirectoryCardBinding binding;

        ViewHolder(ItemDirectoryCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Object obj, Mode mode, OnHostelActionListener hostelActionListener) {
            Context ctx = itemView.getContext();

            if (mode == Mode.HOSTELS && obj instanceof HostelDto) {
                HostelDto h = (HostelDto) obj;
                binding.tvIconBadge.setText("🏢");
                binding.tvPrimaryTitle.setText(h.getName());
                binding.tvSecondarySubtitle.setText((h.getLocation() != null ? h.getLocation() : "Campus") +
                        (h.getDescription() != null && !h.getDescription().isEmpty() ? " · " + h.getDescription() : ""));

                boolean isWardenAssigned = h.isWardenAssigned() || (h.getWardenName() != null && !h.getWardenName().trim().isEmpty());
                if (isWardenAssigned) {
                    binding.tvStatusTag.setText("WARDEN ASSIGNED");
                    binding.tvStatusTag.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFDCFCE7));
                    binding.tvStatusTag.setTextColor(0xFF16A34A);
                    binding.tvMetricLine2.setText("🛡 Warden: " + h.getWardenName() +
                            (h.getWardenPhone() != null && !h.getWardenPhone().isEmpty() ? " (" + h.getWardenPhone() + ")" : ""));
                } else {
                    binding.tvStatusTag.setText("NO WARDEN");
                    binding.tvStatusTag.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFFEF3C7));
                    binding.tvStatusTag.setTextColor(0xFFD97706);
                    binding.tvMetricLine2.setText("⚠️ No Warden Assigned - Tap to assign");
                }

                String line1 = "👥 Residents: " + h.getStudentCount() + " · ⚠ Open: " + h.getOpenIssuesCount() + " · ✓ Solved: " + h.getResolvedIssuesCount();
                binding.tvMetricLine1.setText(line1);

                binding.btnCardAction.setVisibility(View.VISIBLE);
                binding.btnCardAction.setText(isWardenAssigned ? "⚙ Manage" : "🛡 Assign");

                View.OnClickListener menuClick = v -> {
                    if (hostelActionListener == null) return;
                    String[] options = new String[]{
                            isWardenAssigned ? "🛡 Change Assigned Warden" : "🛡 Assign Warden",
                            "✏️ Edit Hostel Details",
                            "🗑 Delete Hostel"
                    };
                    new AlertDialog.Builder(ctx)
                            .setTitle(h.getName() + " · Options")
                            .setItems(options, (dialog, which) -> {
                                if (which == 0) {
                                    hostelActionListener.onAssignWarden(h);
                                } else if (which == 1) {
                                    hostelActionListener.onEditHostel(h);
                                } else if (which == 2) {
                                    hostelActionListener.onDeleteHostel(h);
                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                };

                binding.btnCardAction.setOnClickListener(menuClick);
                binding.getRoot().setOnClickListener(menuClick);

            } else if (mode == Mode.CREWS && obj instanceof CrewWorkloadDto) {
                CrewWorkloadDto c = (CrewWorkloadDto) obj;
                binding.tvIconBadge.setText("🔧");
                binding.tvPrimaryTitle.setText(c.getDisplayName() != null ? c.getDisplayName() : c.getName());
                binding.tvSecondarySubtitle.setText(c.getDescription() != null ? c.getDescription() : "Facilities Maintenance Crew");

                boolean hasActive = c.getActiveTasks() > 0;
                binding.tvStatusTag.setText(hasActive ? c.getActiveTasks() + " IN ACTION" : "READY");
                binding.tvStatusTag.setBackgroundTintList(android.content.res.ColorStateList.valueOf(hasActive ? 0xFFFFE4E6 : 0xFFDCFCE7));
                binding.tvStatusTag.setTextColor(hasActive ? 0xFFDC2626 : 0xFF16A34A);

                String line1 = "👷 Staff on Duty: " + c.getStaffCount() + " · ⚡ Active: " + c.getActiveTasks() + " · ✓ Solved: " + c.getResolvedTasks();
                binding.tvMetricLine1.setText(line1);

                String staffNames = c.getStaffNames() != null && !c.getStaffNames().isEmpty() ?
                        "Crews: " + String.join(", ", c.getStaffNames()) : "No technicians assigned yet";
                binding.tvMetricLine2.setText(staffNames);
                binding.btnCardAction.setVisibility(View.GONE);
                binding.getRoot().setOnClickListener(null);

            } else if (mode == Mode.STUDENTS && obj instanceof UserDto) {
                UserDto s = (UserDto) obj;
                binding.tvIconBadge.setText("🎓");
                binding.tvPrimaryTitle.setText(s.getFullName());

                String hostelInfo = s.getHostelName() != null ? s.getHostelName() : "Campus Residence";
                String roomInfo = s.getRoomNumber() != null ? " · Room " + s.getRoomNumber() : "";
                String batchTag = (s.getBatch() != null && !s.getBatch().trim().isEmpty()) ? " · Batch " + s.getBatch() : "";
                binding.tvSecondarySubtitle.setText(hostelInfo + roomInfo + batchTag);

                long comp = s.getTotalComplaints();
                binding.tvStatusTag.setText(comp + (comp == 1 ? " TICKET" : " TICKETS"));
                binding.tvStatusTag.setBackgroundTintList(android.content.res.ColorStateList.valueOf(comp > 0 ? 0xFFFEF3C7 : 0xFFF3F4F6));
                binding.tvStatusTag.setTextColor(comp > 0 ? 0xFFD97706 : 0xFF6B7280);

                String batchStr = (s.getBatch() != null && !s.getBatch().trim().isEmpty()) ? " · Batch: " + s.getBatch() : "";
                String line1 = "ID: " + (s.getInstitutionalId() != null ? s.getInstitutionalId() : "N/A") + batchStr +
                        (s.getEmail() != null ? " · " + s.getEmail() : "");
                binding.tvMetricLine1.setText(line1);

                String line2 = "Active Complaints: " + s.getActiveComplaints() + " · Total Complaints: " + s.getTotalComplaints();
                binding.tvMetricLine2.setText(line2);

                if (s.getPhone() != null && !s.getPhone().trim().isEmpty()) {
                    binding.btnCardAction.setVisibility(View.VISIBLE);
                    binding.btnCardAction.setText("📞 Call");
                    binding.btnCardAction.setOnClickListener(v -> dialPhone(ctx, s.getPhone()));
                } else {
                    binding.btnCardAction.setVisibility(View.GONE);
                }
                binding.getRoot().setOnClickListener(null);

            } else if (mode == Mode.STAFF && obj instanceof UserDto) {
                UserDto st = (UserDto) obj;
                String role = st.getRole() != null ? st.getRole() : "STAFF";
                binding.tvIconBadge.setText("WARDEN".equalsIgnoreCase(role) ? "🛡" : "🔧");
                binding.tvPrimaryTitle.setText(st.getFullName());

                String assign = "WARDEN".equalsIgnoreCase(role) ?
                        ("Warden · " + (st.getHostelName() != null ? st.getHostelName() : "All Halls")) :
                        ("Staff · " + (st.getDepartmentName() != null ? st.getDepartmentName() : "General Maintenance"));
                binding.tvSecondarySubtitle.setText(assign);

                binding.tvStatusTag.setText(st.getStatus() != null ? st.getStatus() : "ACTIVE");
                binding.tvStatusTag.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFDCFCE7));
                binding.tvStatusTag.setTextColor(0xFF16A34A);

                String line1 = "ID: " + (st.getInstitutionalId() != null ? st.getInstitutionalId() : "N/A") +
                        (st.getEmail() != null ? " · " + st.getEmail() : "");
                binding.tvMetricLine1.setText(line1);

                String phoneText = st.getPhone() != null && !st.getPhone().trim().isEmpty() ?
                        "Phone: " + st.getPhone() : "No contact number recorded";
                binding.tvMetricLine2.setText(phoneText);

                if (st.getPhone() != null && !st.getPhone().trim().isEmpty()) {
                    binding.btnCardAction.setVisibility(View.VISIBLE);
                    binding.btnCardAction.setText("📞 Call");
                    binding.btnCardAction.setOnClickListener(v -> dialPhone(ctx, st.getPhone()));
                } else {
                    binding.btnCardAction.setVisibility(View.GONE);
                }
                binding.getRoot().setOnClickListener(null);
            }
        }

        private void dialPhone(Context context, String phone) {
            if (phone == null || phone.trim().isEmpty()) return;
            try {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone.trim()));
                context.startActivity(intent);
            } catch (Exception e) {
                android.widget.Toast.makeText(context, "Cannot dial: " + phone, android.widget.Toast.LENGTH_SHORT).show();
            }
        }
    }
}
