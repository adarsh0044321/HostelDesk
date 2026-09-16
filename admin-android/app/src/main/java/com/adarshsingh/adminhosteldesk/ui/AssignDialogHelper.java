package com.adarshsingh.adminhosteldesk.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import com.adarshsingh.adminhosteldesk.data.api.ApiClient;
import com.adarshsingh.adminhosteldesk.data.model.AssignIssueRequest;
import com.adarshsingh.adminhosteldesk.data.model.DepartmentDto;
import com.adarshsingh.adminhosteldesk.data.model.IssueDetailDto;
import com.adarshsingh.adminhosteldesk.data.model.IssueDto;
import com.adarshsingh.adminhosteldesk.data.model.UserDto;
import com.adarshsingh.adminhosteldesk.databinding.DialogAssignStaffBinding;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AssignDialogHelper {

    public interface OnAssignmentCompleteListener {
        void onAssigned();
    }

    public static void showAssignDialog(Context context, IssueDto ticket, OnAssignmentCompleteListener listener) {
        DialogAssignStaffBinding binding = DialogAssignStaffBinding.inflate(LayoutInflater.from(context));

        binding.tvDialogTicketInfo.setText("Ticket #" + (ticket.getTicketNumber() != null ? ticket.getTicketNumber() : "HD-" + ticket.getId()) +
                " · " + ticket.getTitle());

        List<DepartmentDto> departmentList = new ArrayList<>();
        List<String> departmentDisplayNames = new ArrayList<>();

        List<UserDto> staffList = new ArrayList<>();
        List<String> staffDisplayNames = new ArrayList<>();

        // Helper to refresh staff dropdown based on selected department
        Runnable refreshStaffDropdown = () -> {
            staffDisplayNames.clear();
            staffDisplayNames.add("Route to Department (Unassigned Staff)");

            String selectedDeptStr = binding.autoCompleteDialogDepartment.getText().toString().toLowerCase();

            // First add staff from selected department
            for (UserDto u : staffList) {
                String uDept = (u.getDepartmentName() != null) ? u.getDepartmentName().toLowerCase() : "";
                if (!uDept.isEmpty() && (selectedDeptStr.contains(uDept) || uDept.contains(selectedDeptStr))) {
                    staffDisplayNames.add(u.getFullName() + " (" + (u.getDepartmentName() != null ? u.getDepartmentName() : "General") + ")");
                }
            }

            // Then add all other staff
            for (UserDto u : staffList) {
                String entry = u.getFullName() + " (" + (u.getDepartmentName() != null ? u.getDepartmentName() : "General") + ")";
                if (!staffDisplayNames.contains(entry)) {
                    staffDisplayNames.add(entry);
                }
            }

            ArrayAdapter<String> staffAdapter = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, staffDisplayNames);
            binding.autoCompleteDialogStaff.setAdapter(staffAdapter);
            if (!staffDisplayNames.isEmpty()) {
                binding.autoCompleteDialogStaff.setText(staffDisplayNames.get(0), false);
            }
        };

        // Determine pre-selected department index
        String currentDeptOrCategory = "";
        if (ticket.getAssignedDepartmentName() != null && !ticket.getAssignedDepartmentName().trim().isEmpty()) {
            currentDeptOrCategory = ticket.getAssignedDepartmentName().toLowerCase();
        } else if (ticket.getCategory() != null && !ticket.getCategory().trim().isEmpty()) {
            currentDeptOrCategory = ticket.getCategory().toLowerCase();
        }

        // Fetch Departments
        final String finalPreselect = currentDeptOrCategory;
        ApiClient.getApiService(context).getDepartments().enqueue(new Callback<List<DepartmentDto>>() {
            @Override
            public void onResponse(Call<List<DepartmentDto>> call, Response<List<DepartmentDto>> response) {
                departmentList.clear();
                departmentDisplayNames.clear();

                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    departmentList.addAll(response.body());
                    int preselectIdx = 0;
                    for (int i = 0; i < departmentList.size(); i++) {
                        DepartmentDto d = departmentList.get(i);
                        String label = d.getDisplayName() != null ? d.getDisplayName() : d.getName();
                        departmentDisplayNames.add(label);
                        if (!finalPreselect.isEmpty() && (label.toLowerCase().contains(finalPreselect) ||
                                (d.getName() != null && d.getName().toLowerCase().contains(finalPreselect)))) {
                            preselectIdx = i;
                        }
                    }

                    ArrayAdapter<String> deptAdapter = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, departmentDisplayNames);
                    binding.autoCompleteDialogDepartment.setAdapter(deptAdapter);
                    if (!departmentDisplayNames.isEmpty()) {
                        binding.autoCompleteDialogDepartment.setText(departmentDisplayNames.get(preselectIdx), false);
                    }
                } else {
                    setupFallbackDepartments(binding, context, finalPreselect, departmentList, departmentDisplayNames);
                }
                refreshStaffDropdown.run();
            }

            @Override
            public void onFailure(Call<List<DepartmentDto>> call, Throwable t) {
                setupFallbackDepartments(binding, context, finalPreselect, departmentList, departmentDisplayNames);
                refreshStaffDropdown.run();
            }
        });

        // Fetch Staff
        ApiClient.getApiService(context).getStaffList().enqueue(new Callback<List<UserDto>>() {
            @Override
            public void onResponse(Call<List<UserDto>> call, Response<List<UserDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    staffList.clear();
                    staffList.addAll(response.body());
                }
                refreshStaffDropdown.run();
            }

            @Override
            public void onFailure(Call<List<UserDto>> call, Throwable t) {
                refreshStaffDropdown.run();
            }
        });

        binding.autoCompleteDialogDepartment.setOnItemClickListener((parent, view, position, id) -> refreshStaffDropdown.run());

        new AlertDialog.Builder(context)
                .setView(binding.getRoot())
                .setPositiveButton("Dispatch & Assign", (dialog, which) -> {
                    String selectedDeptStr = binding.autoCompleteDialogDepartment.getText().toString();
                    Long targetDeptId = 1L;

                    for (DepartmentDto d : departmentList) {
                        String label = d.getDisplayName() != null ? d.getDisplayName() : d.getName();
                        if (label.equalsIgnoreCase(selectedDeptStr) || (d.getName() != null && d.getName().equalsIgnoreCase(selectedDeptStr))) {
                            targetDeptId = d.getId();
                            break;
                        }
                    }

                    Long targetStaffId = null;
                    String selectedStaffStr = binding.autoCompleteDialogStaff.getText().toString();
                    for (UserDto u : staffList) {
                        if (u.getFullName() != null && selectedStaffStr.startsWith(u.getFullName())) {
                            targetStaffId = u.getId();
                            break;
                        }
                    }

                    com.adarshsingh.adminhosteldesk.data.local.SessionManager sm = com.adarshsingh.adminhosteldesk.data.local.SessionManager.getInstance(context);
                    boolean isInstAdmin = sm.getUser() != null && sm.getUser().getRole() != null &&
                            (sm.getUser().getRole().contains("INSTITUTE_ADMIN") || sm.getUser().getRole().contains("SUPER_ADMIN") ||
                            (sm.getUser().getRole().contains("ADMIN") && !sm.getUser().getRole().contains("WARDEN")));
                    String dispatchNote = isInstAdmin ? "Assigned via Institute Console dispatch" : "Assigned via Warden Desk dispatch";

                    AssignIssueRequest request = new AssignIssueRequest(
                            targetDeptId,
                            targetStaffId,
                            ticket.getPriority() != null ? ticket.getPriority() : "MEDIUM",
                            dispatchNote
                    );

                    ApiClient.getApiService(context).assignIssue(ticket.getId(), request)
                            .enqueue(new Callback<IssueDetailDto>() {
                                @Override
                                public void onResponse(Call<IssueDetailDto> call, Response<IssueDetailDto> response) {
                                    if (response.isSuccessful()) {
                                        Toast.makeText(context, "Technician successfully dispatched!", Toast.LENGTH_SHORT).show();
                                        if (listener != null) listener.onAssigned();
                                    } else {
                                        Toast.makeText(context, "Assignment failed (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<IssueDetailDto> call, Throwable t) {
                                    Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private static void setupFallbackDepartments(DialogAssignStaffBinding binding, Context context, String preselect,
                                                  List<DepartmentDto> departmentList, List<String> departmentDisplayNames) {
        String[] defaultNames = new String[]{"Plumbing & Water Supply", "Electrical Facilities", "Carpentry & Furniture", "Housekeeping & Cleanliness"};
        Long[] defaultIds = new Long[]{1L, 2L, 3L, 4L};
        departmentList.clear();
        departmentDisplayNames.clear();
        int preselectIdx = 0;

        for (int i = 0; i < defaultNames.length; i++) {
            DepartmentDto d = new DepartmentDto();
            d.setId(defaultIds[i]);
            d.setName(defaultNames[i]);
            d.setDisplayName(defaultNames[i]);
            departmentList.add(d);
            departmentDisplayNames.add(defaultNames[i]);
            if (!preselect.isEmpty() && defaultNames[i].toLowerCase().contains(preselect)) {
                preselectIdx = i;
            }
        }

        ArrayAdapter<String> deptAdapter = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, departmentDisplayNames);
        binding.autoCompleteDialogDepartment.setAdapter(deptAdapter);
        binding.autoCompleteDialogDepartment.setText(departmentDisplayNames.get(preselectIdx), false);
    }
}
