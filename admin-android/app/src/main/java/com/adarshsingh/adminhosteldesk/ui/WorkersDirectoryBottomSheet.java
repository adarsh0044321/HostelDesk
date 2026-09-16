package com.adarshsingh.adminhosteldesk.ui;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.adarshsingh.adminhosteldesk.R;
import com.adarshsingh.adminhosteldesk.data.api.ApiClient;
import com.adarshsingh.adminhosteldesk.data.model.UserDto;
import com.adarshsingh.adminhosteldesk.databinding.ItemWorkerCardBinding;
import com.adarshsingh.adminhosteldesk.databinding.LayoutWorkersDirectoryBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WorkersDirectoryBottomSheet extends BottomSheetDialogFragment {

    private LayoutWorkersDirectoryBinding binding;
    private final List<UserDto> allStaffList = new ArrayList<>();
    private final List<UserDto> displayedStaffList = new ArrayList<>();
    private WorkerAdapter adapter;
    private String preselectedDepartment = "ALL";

    public static WorkersDirectoryBottomSheet newInstance(@Nullable String initialDepartment) {
        WorkersDirectoryBottomSheet sheet = new WorkersDirectoryBottomSheet();
        Bundle args = new Bundle();
        if (initialDepartment != null) {
            args.putString("initial_dept", initialDepartment);
        }
        sheet.setArguments(args);
        return sheet;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = LayoutWorkersDirectoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null && getArguments().containsKey("initial_dept")) {
            preselectedDepartment = getArguments().getString("initial_dept", "ALL");
        }

        setupRecyclerView();
        setupFilterChips();
        fetchStaff();
    }

    private void setupRecyclerView() {
        adapter = new WorkerAdapter(displayedStaffList, staff -> {
            String phone = staff.getPhone();
            if (phone != null && !phone.trim().isEmpty()) {
                try {
                    Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone.trim()));
                    startActivity(dialIntent);
                } catch (Exception e) {
                    Toast.makeText(requireContext(), "Unable to open dialer: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(requireContext(), "No phone number available for " + staff.getFullName(), Toast.LENGTH_SHORT).show();
            }
        });

        binding.rvWorkersList.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvWorkersList.setAdapter(adapter);
    }

    private void setupFilterChips() {
        binding.chipGroupDepartments.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int checkedId = checkedIds.get(0);
            if (checkedId == R.id.chipPlumbing) {
                applyFilter("plumbing");
            } else if (checkedId == R.id.chipCarpentry) {
                applyFilter("carpentry");
            } else if (checkedId == R.id.chipElectrical) {
                applyFilter("electrical");
            } else if (checkedId == R.id.chipCleaning) {
                applyFilter("clean");
            } else {
                applyFilter("ALL");
            }
        });

        // Set initial chip check if preselected
        if ("plumbing".equalsIgnoreCase(preselectedDepartment)) {
            binding.chipPlumbing.setChecked(true);
        } else if ("carpentry".equalsIgnoreCase(preselectedDepartment)) {
            binding.chipCarpentry.setChecked(true);
        } else if ("electrical".equalsIgnoreCase(preselectedDepartment)) {
            binding.chipElectrical.setChecked(true);
        } else if ("cleaning".equalsIgnoreCase(preselectedDepartment) || "clean".equalsIgnoreCase(preselectedDepartment)) {
            binding.chipCleaning.setChecked(true);
        } else {
            binding.chipAll.setChecked(true);
        }
    }

    private void fetchStaff() {
        binding.progressWorkers.setVisibility(View.VISIBLE);
        binding.tvEmptyWorkers.setVisibility(View.GONE);

        ApiClient.getApiService(requireContext()).getStaffList().enqueue(new Callback<List<UserDto>>() {
            @Override
            public void onResponse(Call<List<UserDto>> call, Response<List<UserDto>> response) {
                if (!isAdded()) return;
                binding.progressWorkers.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    allStaffList.clear();
                    allStaffList.addAll(response.body());
                    binding.tvTotalStaffBadge.setText(allStaffList.size() + " Crew");
                    applyFilter(preselectedDepartment);
                } else {
                    binding.tvEmptyWorkers.setText("Failed to load maintenance crew list");
                    binding.tvEmptyWorkers.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<UserDto>> call, Throwable t) {
                if (!isAdded()) return;
                binding.progressWorkers.setVisibility(View.GONE);
                binding.tvEmptyWorkers.setText("Connection error: " + t.getMessage());
                binding.tvEmptyWorkers.setVisibility(View.VISIBLE);
            }
        });
    }

    private void applyFilter(String filter) {
        displayedStaffList.clear();
        if ("ALL".equalsIgnoreCase(filter) || filter == null || filter.trim().isEmpty()) {
            displayedStaffList.addAll(allStaffList);
        } else {
            String lower = filter.toLowerCase();
            for (UserDto u : allStaffList) {
                String d = u.getDepartmentName() != null ? u.getDepartmentName().toLowerCase() : "";
                if (d.contains(lower) || lower.contains(d)) {
                    displayedStaffList.add(u);
                }
            }
        }

        adapter.notifyDataSetChanged();

        if (displayedStaffList.isEmpty()) {
            binding.tvEmptyWorkers.setVisibility(View.VISIBLE);
            binding.rvWorkersList.setVisibility(View.GONE);
        } else {
            binding.tvEmptyWorkers.setVisibility(View.GONE);
            binding.rvWorkersList.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // Inner Adapter
    static class WorkerAdapter extends RecyclerView.Adapter<WorkerAdapter.WorkerHolder> {
        private final List<UserDto> list;
        private final OnCallListener callListener;

        interface OnCallListener {
            void onCall(UserDto staff);
        }

        WorkerAdapter(List<UserDto> list, OnCallListener callListener) {
            this.list = list;
            this.callListener = callListener;
        }

        @NonNull
        @Override
        public WorkerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemWorkerCardBinding b = ItemWorkerCardBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false
            );
            return new WorkerHolder(b);
        }

        @Override
        public void onBindViewHolder(@NonNull WorkerHolder holder, int position) {
            holder.bind(list.get(position), callListener);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        static class WorkerHolder extends RecyclerView.ViewHolder {
            private final ItemWorkerCardBinding b;

            WorkerHolder(ItemWorkerCardBinding b) {
                super(b.getRoot());
                this.b = b;
            }

            void bind(UserDto staff, OnCallListener listener) {
                String name = staff.getFullName() != null ? staff.getFullName() : "Technician";
                b.tvWorkerName.setText(name);

                String initial = name.isEmpty() ? "W" : String.valueOf(name.charAt(0)).toUpperCase();
                b.tvWorkerInitial.setText(initial);

                String dept = staff.getDepartmentName() != null ? staff.getDepartmentName().toUpperCase() : "GENERAL";
                b.tvWorkerDeptTag.setText(dept);

                long active = staff.getActiveComplaints();
                if (active > 0) {
                    b.tvWorkerWorkloadBadge.setText("● " + active + (active == 1 ? " Active Task" : " Active Tasks"));
                    b.tvWorkerWorkloadBadge.setTextColor(0xFFB43B2B);
                    b.tvWorkerWorkloadBadge.setBackgroundTintList(ColorStateList.valueOf(0xFFFFF1EB));
                } else {
                    b.tvWorkerWorkloadBadge.setText("● Available");
                    b.tvWorkerWorkloadBadge.setTextColor(0xFF0F6746);
                    b.tvWorkerWorkloadBadge.setBackgroundTintList(ColorStateList.valueOf(0xFFEBF5F0));
                }

                String hostel = staff.getHostelName() != null ? staff.getHostelName() : "Campus Facilities";
                b.tvWorkerHostel.setText(hostel);

                String phone = staff.getPhone() != null && !staff.getPhone().trim().isEmpty() ?
                        staff.getPhone() : (staff.getEmail() != null ? staff.getEmail() : "No contact recorded");
                b.tvWorkerContact.setText("📞 " + phone);

                b.btnCallWorker.setOnClickListener(v -> {
                    if (listener != null) listener.onCall(staff);
                });
            }
        }
    }
}
