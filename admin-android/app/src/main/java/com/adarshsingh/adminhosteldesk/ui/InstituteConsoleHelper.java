package com.adarshsingh.adminhosteldesk.ui;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Typeface;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import com.adarshsingh.adminhosteldesk.R;
import com.adarshsingh.adminhosteldesk.data.api.ApiClient;
import android.content.Intent;
import android.net.Uri;
import com.adarshsingh.adminhosteldesk.data.model.AnnouncementDto;
import com.adarshsingh.adminhosteldesk.data.model.CreateAnnouncementRequest;
import com.adarshsingh.adminhosteldesk.data.model.CreateHostelRequest;
import com.adarshsingh.adminhosteldesk.data.model.AssignWardenRequest;
import com.adarshsingh.adminhosteldesk.data.model.CreateUserWithTempPasswordRequest;
import com.adarshsingh.adminhosteldesk.data.model.CredentialResponse;
import com.adarshsingh.adminhosteldesk.data.model.DepartmentDto;
import com.adarshsingh.adminhosteldesk.data.model.EmergencyContactsDto;
import com.adarshsingh.adminhosteldesk.data.model.HostelDto;
import com.adarshsingh.adminhosteldesk.data.model.PasswordResetDto;
import com.adarshsingh.adminhosteldesk.data.model.UpdateContactRequest;
import com.adarshsingh.adminhosteldesk.data.model.UserDto;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InstituteConsoleHelper {

    public static void showInstituteManagementDialog(Context context) {
        String role = com.adarshsingh.adminhosteldesk.data.local.SessionManager.getInstance(context).getRole();
        boolean isWarden = "WARDEN".equalsIgnoreCase(role);

        if (isWarden) {
            String[] wardenOptions = new String[]{
                    "🔧 Onboard Maintenance Staff",
                    "📢 Post Hostel Notice"
            };
            new AlertDialog.Builder(context)
                    .setTitle("Hostel Operations & Notices")
                    .setIcon(R.drawable.ic_domain)
                    .setItems(wardenOptions, (dialog, which) -> {
                        if (which == 0) {
                            showOnboardStaffDialog(context, null);
                        } else if (which == 1) {
                            showPostAnnouncementDialog(context, true);
                        }
                    })
                    .setNegativeButton("Close", null)
                    .show();
            return;
        }

        String[] options = new String[]{
                "🎓 Onboard Student",
                "🛡 Onboard Warden",
                "🔧 Onboard Maintenance Staff",
                "🏢 Add New Hostel / Residence Hall",
                "📢 Broadcast Campus Announcement",
                "🚨 Configure Emergency Helplines",
                "📂 Open Campus & Crew Directory",
                "🔑 Password Resets & IT Recovery"
        };

        new AlertDialog.Builder(context)
                .setTitle("Institute Administration & Onboarding")
                .setIcon(R.drawable.ic_domain)
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        showOnboardStudentDialog(context, null);
                    } else if (which == 1) {
                        showOnboardWardenDialog(context, null);
                    } else if (which == 2) {
                        showOnboardStaffDialog(context, null);
                    } else if (which == 3) {
                        showCreateHostelDialog(context, null);
                    } else if (which == 4) {
                        showPostAnnouncementDialog(context, false);
                    } else if (which == 5) {
                        showEmergencyContactsDialog(context);
                    } else if (which == 6) {
                        if (context instanceof AdminMainActivity) {
                            ((AdminMainActivity) context).switchToTab(R.id.nav_staff_work);
                        }
                    } else if (which == 7) {
                        showPasswordResetsDialog(context);
                    }
                })
                .setNegativeButton("Close", null)
                .show();
    }

    public static void showOnboardStudentDialog(Context context, Runnable onSuccess) {
        ScrollView scrollView = new ScrollView(context);
        LinearLayout layout = createFormContainer(context);
        scrollView.addView(layout);

        addHeader(context, layout, "Onboard New Student", "Provisions account and generates credentials");

        EditText etName = addField(context, layout, "Full Name *", "e.g. Rahul Sharma", InputType.TYPE_CLASS_TEXT);
        EditText etEmail = addField(context, layout, "Student Email *", "e.g. rahul.sharma@campus.edu", InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        EditText etPhone = addField(context, layout, "Phone Number", "e.g. +91 98765 43210", InputType.TYPE_CLASS_PHONE);
        EditText etStudentId = addField(context, layout, "Student Roll No / ID (optional)", "e.g. CS2026-042 (auto if blank)", InputType.TYPE_CLASS_TEXT);

        TextView tvHostel = new TextView(context);
        tvHostel.setText("Assigned Hostel");
        tvHostel.setTextColor(0xFF515F74);
        tvHostel.setTextSize(12);
        tvHostel.setTypeface(null, Typeface.BOLD);
        tvHostel.setPadding(0, dp(context, 8), 0, dp(context, 4));
        layout.addView(tvHostel);

        Spinner spHostel = new Spinner(context);
        spHostel.setPadding(0, dp(context, 6), 0, dp(context, 10));
        layout.addView(spHostel);

        List<HostelDto> hostelList = new ArrayList<>();
        ArrayAdapter<String> hostelAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, new ArrayList<>());
        spHostel.setAdapter(hostelAdapter);

        // Fetch hostels
        ApiClient.getApiService(context).getHostels().enqueue(new Callback<List<HostelDto>>() {
            @Override
            public void onResponse(Call<List<HostelDto>> call, Response<List<HostelDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    hostelList.clear();
                    hostelList.addAll(response.body());
                    List<String> names = new ArrayList<>();
                    for (HostelDto h : hostelList) {
                        names.add(h.getName());
                    }
                    if (names.isEmpty()) names.add("Default Residence Hall");
                    hostelAdapter.clear();
                    hostelAdapter.addAll(names);
                    hostelAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<HostelDto>> call, Throwable t) {}
        });

        EditText etRoom = addField(context, layout, "Room Number", "e.g. B-304", InputType.TYPE_CLASS_TEXT);
        EditText etBlock = addField(context, layout, "Block / Wing", "e.g. North Wing", InputType.TYPE_CLASS_TEXT);
        EditText etBatch = addField(context, layout, "Academic Batch (optional)", "e.g. 2024-2028 or Batch 2026", InputType.TYPE_CLASS_TEXT);

        new AlertDialog.Builder(context)
                .setView(scrollView)
                .setPositiveButton("Create Student Account", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    String email = etEmail.getText().toString().trim();
                    String phone = etPhone.getText().toString().trim();
                    String studentId = etStudentId.getText().toString().trim();
                    String room = etRoom.getText().toString().trim();
                    String block = etBlock.getText().toString().trim();
                    String batch = etBatch.getText().toString().trim();

                    if (name.isEmpty() || email.isEmpty()) {
                        Toast.makeText(context, "Full Name and Email are required.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    CreateUserWithTempPasswordRequest req = new CreateUserWithTempPasswordRequest();
                    req.setFullName(name);
                    req.setEmail(email);
                    req.setPhone(phone.isEmpty() ? null : phone);
                    req.setInstitutionalId(studentId.isEmpty() ? null : studentId);
                    req.setRoomNumber(room.isEmpty() ? null : room);
                    req.setBlockName(block.isEmpty() ? null : block);
                    req.setBatch(batch.isEmpty() ? null : batch);

                    int selectedHostelPos = spHostel.getSelectedItemPosition();
                    if (selectedHostelPos >= 0 && selectedHostelPos < hostelList.size()) {
                        req.setHostelId(hostelList.get(selectedHostelPos).getId());
                    }

                    ProgressDialog progress = ProgressDialog.show(context, "", "Creating student account...", true);
                    ApiClient.getApiService(context).createStudent(req).enqueue(new Callback<CredentialResponse>() {
                        @Override
                        public void onResponse(Call<CredentialResponse> call, Response<CredentialResponse> response) {
                            progress.dismiss();
                            if (response.isSuccessful() && response.body() != null) {
                                showCredentialDialog(context, "Student Account Created", response.body());
                                if (onSuccess != null) onSuccess.run();
                            } else {
                                showError(context, "Failed to create student: " + extractError(response));
                            }
                        }

                        @Override
                        public void onFailure(Call<CredentialResponse> call, Throwable t) {
                            progress.dismiss();
                            showError(context, "Network error: " + t.getMessage());
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    public static void showOnboardWardenDialog(Context context, Runnable onSuccess) {
        ScrollView scrollView = new ScrollView(context);
        LinearLayout layout = createFormContainer(context);
        scrollView.addView(layout);

        addHeader(context, layout, "Onboard New Warden", "Grants warden administrative oversight");

        EditText etName = addField(context, layout, "Full Name *", "e.g. Dr. Rajesh Kumar", InputType.TYPE_CLASS_TEXT);
        EditText etEmail = addField(context, layout, "Official Email *", "e.g. warden.rajesh@campus.edu", InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        EditText etPhone = addField(context, layout, "Contact Phone", "e.g. +91 98765 11223", InputType.TYPE_CLASS_PHONE);
        EditText etWardenId = addField(context, layout, "Warden ID (optional)", "e.g. WRD-002 (auto if blank)", InputType.TYPE_CLASS_TEXT);

        TextView tvHostel = new TextView(context);
        tvHostel.setText("Assigned Hostel / Hall");
        tvHostel.setTextColor(0xFF515F74);
        tvHostel.setTextSize(12);
        tvHostel.setTypeface(null, Typeface.BOLD);
        tvHostel.setPadding(0, dp(context, 8), 0, dp(context, 4));
        layout.addView(tvHostel);

        Spinner spHostel = new Spinner(context);
        spHostel.setPadding(0, dp(context, 6), 0, dp(context, 10));
        layout.addView(spHostel);

        List<HostelDto> hostelList = new ArrayList<>();
        ArrayAdapter<String> hostelAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, new ArrayList<>());
        spHostel.setAdapter(hostelAdapter);

        ApiClient.getApiService(context).getHostels().enqueue(new Callback<List<HostelDto>>() {
            @Override
            public void onResponse(Call<List<HostelDto>> call, Response<List<HostelDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    hostelList.clear();
                    hostelList.addAll(response.body());
                    List<String> names = new ArrayList<>();
                    for (HostelDto h : hostelList) {
                        names.add(h.getName());
                    }
                    if (names.isEmpty()) names.add("Campus Hall");
                    hostelAdapter.clear();
                    hostelAdapter.addAll(names);
                    hostelAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<HostelDto>> call, Throwable t) {}
        });

        new AlertDialog.Builder(context)
                .setView(scrollView)
                .setPositiveButton("Create Warden Account", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    String email = etEmail.getText().toString().trim();
                    String phone = etPhone.getText().toString().trim();
                    String wardenId = etWardenId.getText().toString().trim();

                    if (name.isEmpty() || email.isEmpty()) {
                        Toast.makeText(context, "Full Name and Email are required.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    CreateUserWithTempPasswordRequest req = new CreateUserWithTempPasswordRequest();
                    req.setFullName(name);
                    req.setEmail(email);
                    req.setPhone(phone.isEmpty() ? null : phone);
                    req.setInstitutionalId(wardenId.isEmpty() ? null : wardenId);

                    int selectedHostelPos = spHostel.getSelectedItemPosition();
                    if (selectedHostelPos >= 0 && selectedHostelPos < hostelList.size()) {
                        req.setHostelId(hostelList.get(selectedHostelPos).getId());
                    }

                    ProgressDialog progress = ProgressDialog.show(context, "", "Creating warden account...", true);
                    ApiClient.getApiService(context).createWarden(req).enqueue(new Callback<CredentialResponse>() {
                        @Override
                        public void onResponse(Call<CredentialResponse> call, Response<CredentialResponse> response) {
                            progress.dismiss();
                            if (response.isSuccessful() && response.body() != null) {
                                showCredentialDialog(context, "Warden Account Created", response.body());
                                if (onSuccess != null) onSuccess.run();
                            } else {
                                showError(context, "Failed to create warden: " + extractError(response));
                            }
                        }

                        @Override
                        public void onFailure(Call<CredentialResponse> call, Throwable t) {
                            progress.dismiss();
                            showError(context, "Network error: " + t.getMessage());
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    public static void showOnboardStaffDialog(Context context, Runnable onSuccess) {
        ScrollView scrollView = new ScrollView(context);
        LinearLayout layout = createFormContainer(context);
        scrollView.addView(layout);

        addHeader(context, layout, "Onboard Maintenance Staff", "Provisions maintenance technician account");

        EditText etName = addField(context, layout, "Full Name *", "e.g. Amit Verma", InputType.TYPE_CLASS_TEXT);
        EditText etEmail = addField(context, layout, "Staff Email *", "e.g. amit.staff@campus.edu", InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        EditText etPhone = addField(context, layout, "Contact Phone", "e.g. +91 98765 55667", InputType.TYPE_CLASS_PHONE);
        EditText etStaffId = addField(context, layout, "Staff ID (optional)", "e.g. STF-010 (auto if blank)", InputType.TYPE_CLASS_TEXT);

        TextView tvDept = new TextView(context);
        tvDept.setText("Work Field / Department *");
        tvDept.setTextColor(0xFF515F74);
        tvDept.setTextSize(12);
        tvDept.setTypeface(null, Typeface.BOLD);
        tvDept.setPadding(0, dp(context, 8), 0, dp(context, 4));
        layout.addView(tvDept);

        Spinner spDept = new Spinner(context);
        spDept.setPadding(0, dp(context, 6), 0, dp(context, 10));
        layout.addView(spDept);

        List<DepartmentDto> deptList = new ArrayList<>();
        ArrayAdapter<String> deptAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, new ArrayList<>());
        spDept.setAdapter(deptAdapter);

        // Fetch departments
        ApiClient.getApiService(context).getDepartments().enqueue(new Callback<List<DepartmentDto>>() {
            @Override
            public void onResponse(Call<List<DepartmentDto>> call, Response<List<DepartmentDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    deptList.clear();
                    deptList.addAll(response.body());
                    List<String> names = new ArrayList<>();
                    for (DepartmentDto d : deptList) {
                        names.add(d.getDisplayName() != null ? d.getDisplayName() : d.getName());
                    }
                    if (names.isEmpty()) names.add("General Maintenance");
                    deptAdapter.clear();
                    deptAdapter.addAll(names);
                    deptAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<DepartmentDto>> call, Throwable t) {}
        });

        new AlertDialog.Builder(context)
                .setView(scrollView)
                .setPositiveButton("Create Staff Account", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    String email = etEmail.getText().toString().trim();
                    String phone = etPhone.getText().toString().trim();
                    String staffId = etStaffId.getText().toString().trim();

                    if (name.isEmpty() || email.isEmpty()) {
                        Toast.makeText(context, "Full Name and Email are required.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    CreateUserWithTempPasswordRequest req = new CreateUserWithTempPasswordRequest();
                    req.setFullName(name);
                    req.setEmail(email);
                    req.setPhone(phone.isEmpty() ? null : phone);
                    req.setInstitutionalId(staffId.isEmpty() ? null : staffId);

                    int selectedDeptPos = spDept.getSelectedItemPosition();
                    if (selectedDeptPos >= 0 && selectedDeptPos < deptList.size()) {
                        req.setDepartmentId(deptList.get(selectedDeptPos).getId());
                    }

                    ProgressDialog progress = ProgressDialog.show(context, "", "Creating staff account...", true);
                    ApiClient.getApiService(context).createStaff(req).enqueue(new Callback<CredentialResponse>() {
                        @Override
                        public void onResponse(Call<CredentialResponse> call, Response<CredentialResponse> response) {
                            progress.dismiss();
                            if (response.isSuccessful() && response.body() != null) {
                                showCredentialDialog(context, "Staff Account Created", response.body());
                                if (onSuccess != null) onSuccess.run();
                            } else {
                                showError(context, "Failed to create staff: " + extractError(response));
                            }
                        }

                        @Override
                        public void onFailure(Call<CredentialResponse> call, Throwable t) {
                            progress.dismiss();
                            showError(context, "Network error: " + t.getMessage());
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    public static void showCreateHostelDialog(Context context, Runnable onSuccess) {
        ScrollView scrollView = new ScrollView(context);
        LinearLayout layout = createFormContainer(context);
        scrollView.addView(layout);

        addHeader(context, layout, "Add Residence Hall / Hostel", "Create hostel building for room allocation");

        EditText etName = addField(context, layout, "Hostel Name *", "e.g. Tagore Hall of Residence", InputType.TYPE_CLASS_TEXT);
        EditText etLocation = addField(context, layout, "Location / Sector *", "e.g. North Campus Sector 2", InputType.TYPE_CLASS_TEXT);
        EditText etDesc = addField(context, layout, "Description (optional)", "e.g. 4-floor student residence", InputType.TYPE_CLASS_TEXT);

        new AlertDialog.Builder(context)
                .setView(scrollView)
                .setPositiveButton("Create Hostel", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    String loc = etLocation.getText().toString().trim();
                    String desc = etDesc.getText().toString().trim();

                    if (name.isEmpty() || loc.isEmpty()) {
                        Toast.makeText(context, "Hostel Name and Location are required.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    CreateHostelRequest req = new CreateHostelRequest(name, loc, desc);
                    ProgressDialog progress = ProgressDialog.show(context, "", "Creating hostel...", true);
                    ApiClient.getApiService(context).createHostel(req).enqueue(new Callback<HostelDto>() {
                        @Override
                        public void onResponse(Call<HostelDto> call, Response<HostelDto> response) {
                            progress.dismiss();
                            if (response.isSuccessful() && response.body() != null) {
                                new AlertDialog.Builder(context)
                                        .setTitle("Hostel Created")
                                        .setMessage("Hostel '" + response.body().getName() + "' has been successfully registered.")
                                        .setPositiveButton("OK", null)
                                        .show();
                                if (onSuccess != null) onSuccess.run();
                            } else {
                                showError(context, "Failed to create hostel: " + extractError(response));
                            }
                        }

                        @Override
                        public void onFailure(Call<HostelDto> call, Throwable t) {
                            progress.dismiss();
                            showError(context, "Network error: " + t.getMessage());
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    public static void showAssignWardenDialog(Context context, Long hostelId, String hostelName, Runnable onSuccess) {
        ProgressDialog progress = ProgressDialog.show(context, "", "Loading wardens...", true);
        ApiClient.getApiService(context).getInstituteWardens().enqueue(new Callback<List<UserDto>>() {
            @Override
            public void onResponse(Call<List<UserDto>> call, Response<List<UserDto>> response) {
                progress.dismiss();
                if (!response.isSuccessful() || response.body() == null || response.body().isEmpty()) {
                    Toast.makeText(context, "No wardens available. Please onboard a warden first.", Toast.LENGTH_LONG).show();
                    return;
                }

                List<UserDto> wardens = response.body();
                String[] names = new String[wardens.size()];
                for (int i = 0; i < wardens.size(); i++) {
                    UserDto w = wardens.get(i);
                    names[i] = w.getFullName() + " (" + (w.getPhone() != null ? w.getPhone() : w.getEmail()) + ")";
                }

                new AlertDialog.Builder(context)
                        .setTitle("Assign Warden to " + (hostelName != null ? hostelName : "Hostel"))
                        .setItems(names, (dialog, which) -> {
                            UserDto selectedWarden = wardens.get(which);
                            ProgressDialog assigning = ProgressDialog.show(context, "", "Assigning warden...", true);
                            ApiClient.getApiService(context).assignWarden(hostelId, new AssignWardenRequest(selectedWarden.getId())).enqueue(new Callback<HostelDto>() {
                                @Override
                                public void onResponse(Call<HostelDto> c, Response<HostelDto> r) {
                                    assigning.dismiss();
                                    if (r.isSuccessful()) {
                                        Toast.makeText(context, "Assigned " + selectedWarden.getFullName() + " as Warden!", Toast.LENGTH_SHORT).show();
                                        if (onSuccess != null) onSuccess.run();
                                    } else {
                                        showError(context, "Failed to assign warden: " + extractError(r));
                                    }
                                }

                                @Override
                                public void onFailure(Call<HostelDto> c, Throwable t) {
                                    assigning.dismiss();
                                    showError(context, "Network error: " + t.getMessage());
                                }
                            });
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }

            @Override
            public void onFailure(Call<List<UserDto>> call, Throwable t) {
                progress.dismiss();
                showError(context, "Network error: " + t.getMessage());
            }
        });
    }

    public static void showEditHostelDialog(Context context, HostelDto hostel, Runnable onSuccess) {
        ScrollView scrollView = new ScrollView(context);
        LinearLayout layout = createFormContainer(context);
        scrollView.addView(layout);

        addHeader(context, layout, "Edit Hostel / Hall", "Update details for " + hostel.getName());

        EditText etName = addField(context, layout, "Hostel Name *", "e.g. Tagore Hall of Residence", InputType.TYPE_CLASS_TEXT);
        etName.setText(hostel.getName());

        EditText etLocation = addField(context, layout, "Location / Sector *", "e.g. North Campus Sector 2", InputType.TYPE_CLASS_TEXT);
        if (hostel.getLocation() != null) etLocation.setText(hostel.getLocation());

        EditText etDesc = addField(context, layout, "Description (optional)", "e.g. 4-floor student residence", InputType.TYPE_CLASS_TEXT);
        if (hostel.getDescription() != null) etDesc.setText(hostel.getDescription());

        new AlertDialog.Builder(context)
                .setView(scrollView)
                .setPositiveButton("Save Changes", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    String loc = etLocation.getText().toString().trim();
                    String desc = etDesc.getText().toString().trim();

                    if (name.isEmpty() || loc.isEmpty()) {
                        Toast.makeText(context, "Hostel Name and Location are required.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    CreateHostelRequest req = new CreateHostelRequest(name, loc, desc, hostel.getCampusId(), hostel.getActive());
                    ProgressDialog progress = ProgressDialog.show(context, "", "Updating hostel...", true);
                    ApiClient.getApiService(context).updateHostel(hostel.getId(), req).enqueue(new Callback<HostelDto>() {
                        @Override
                        public void onResponse(Call<HostelDto> call, Response<HostelDto> response) {
                            progress.dismiss();
                            if (response.isSuccessful() && response.body() != null) {
                                Toast.makeText(context, "Hostel updated successfully.", Toast.LENGTH_SHORT).show();
                                if (onSuccess != null) onSuccess.run();
                            } else {
                                showError(context, "Failed to update hostel: " + extractError(response));
                            }
                        }

                        @Override
                        public void onFailure(Call<HostelDto> call, Throwable t) {
                            progress.dismiss();
                            showError(context, "Network error: " + t.getMessage());
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    public static void confirmDeleteHostel(Context context, HostelDto hostel, Runnable onSuccess) {
        new AlertDialog.Builder(context)
                .setTitle("Delete " + hostel.getName() + "?")
                .setMessage("Are you sure you want to delete this hostel? All residents and staff will be unlinked from it.")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Delete", (dialog, which) -> {
                    ProgressDialog progress = ProgressDialog.show(context, "", "Deleting hostel...", true);
                    ApiClient.getApiService(context).deleteHostel(hostel.getId()).enqueue(new Callback<Map<String, Object>>() {
                        @Override
                        public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                            progress.dismiss();
                            if (response.isSuccessful()) {
                                Toast.makeText(context, "Hostel deleted successfully.", Toast.LENGTH_SHORT).show();
                                if (onSuccess != null) onSuccess.run();
                            } else {
                                showError(context, "Failed to delete hostel: " + extractError(response));
                            }
                        }

                        @Override
                        public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                            progress.dismiss();
                            showError(context, "Network error: " + t.getMessage());
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    public static void showUpdateWardenContactDialog(Context context, Runnable onSuccess) {
        ScrollView scrollView = new ScrollView(context);
        LinearLayout layout = createFormContainer(context);
        scrollView.addView(layout);

        addHeader(context, layout, "Update Contact Information", "Keep your emergency phone and email updated");

        com.adarshsingh.adminhosteldesk.data.local.SessionManager session =
                com.adarshsingh.adminhosteldesk.data.local.SessionManager.getInstance(context);

        EditText etPhone = addField(context, layout, "Phone Number *", "e.g. +91 98765 43210", InputType.TYPE_CLASS_PHONE);
        if (session.getPhone() != null && !session.getPhone().isEmpty()) {
            etPhone.setText(session.getPhone());
        }

        EditText etEmail = addField(context, layout, "Email Address *", "e.g. warden@campus.edu", InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        if (session.getEmail() != null && !session.getEmail().isEmpty()) {
            etEmail.setText(session.getEmail());
        }

        new AlertDialog.Builder(context)
                .setView(scrollView)
                .setPositiveButton("Save Contact", (dialog, which) -> {
                    String phone = etPhone.getText().toString().trim();
                    String email = etEmail.getText().toString().trim();

                    if (phone.isEmpty() || email.isEmpty()) {
                        Toast.makeText(context, "Phone and Email are required.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    UpdateContactRequest req = new UpdateContactRequest(phone, email);
                    ProgressDialog progress = ProgressDialog.show(context, "", "Updating contact...", true);
                    ApiClient.getApiService(context).updateWardenContact(req).enqueue(new Callback<UserDto>() {
                        @Override
                        public void onResponse(Call<UserDto> call, Response<UserDto> response) {
                            progress.dismiss();
                            if (response.isSuccessful() && response.body() != null) {
                                UserDto updated = response.body();
                                if (updated.getPhone() != null) session.setPhone(updated.getPhone());
                                if (updated.getEmail() != null) session.setEmail(updated.getEmail());
                                Toast.makeText(context, "Contact details updated successfully.", Toast.LENGTH_SHORT).show();
                                if (onSuccess != null) onSuccess.run();
                            } else {
                                showError(context, "Failed to update contact: " + extractError(response));
                            }
                        }

                        @Override
                        public void onFailure(Call<UserDto> call, Throwable t) {
                            progress.dismiss();
                            showError(context, "Network error: " + t.getMessage());
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private static void showCredentialDialog(Context context, String title, CredentialResponse creds) {
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        int pad = dp(context, 16);
        layout.setPadding(pad, pad, pad, pad);

        TextView tvInfo = new TextView(context);
        tvInfo.setText("A new institutional account has been provisioned.\nPlease share these initial credentials securely with the user:");
        tvInfo.setTextSize(13);
        tvInfo.setTextColor(0xFF515F74);
        layout.addView(tvInfo);

        TextView tvCreds = new TextView(context);
        tvCreds.setPadding(0, dp(context, 12), 0, dp(context, 12));
        tvCreds.setTextSize(14);
        tvCreds.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        tvCreds.setTextColor(0xFF1A1C1F);
        String credText = "Name: " + creds.getFullName() + "\n" +
                "ID: " + creds.getInstitutionalId() + "\n" +
                "Email: " + creds.getEmail() + "\n" +
                "Role: " + creds.getRole() + "\n" +
                "Temporary Password: " + creds.getTemporaryPassword();
        tvCreds.setText(credText);
        layout.addView(tvCreds);

        TextView tvNote = new TextView(context);
        tvNote.setText("User will be prompted to change this temporary password upon first login.");
        tvNote.setTextSize(12);
        tvNote.setTextColor(0xFF0F6746);
        layout.addView(tvNote);

        new AlertDialog.Builder(context)
                .setTitle(title)
                .setIcon(R.drawable.ic_security)
                .setView(layout)
                .setPositiveButton("Copy Credentials", (dialog, which) -> {
                    ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("HostelDesk Credentials", credText);
                    if (clipboard != null) {
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(context, "Credentials copied to clipboard!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Close", null)
                .show();
    }

    private static LinearLayout createFormContainer(Context context) {
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        int pad = dp(context, 16);
        layout.setPadding(pad, pad, pad, pad);
        return layout;
    }

    private static void addHeader(Context context, LinearLayout layout, String title, String subtitle) {
        TextView tvTitle = new TextView(context);
        tvTitle.setText(title);
        tvTitle.setTextSize(16);
        tvTitle.setTypeface(null, Typeface.BOLD);
        tvTitle.setTextColor(0xFF1A1C1F);
        layout.addView(tvTitle);

        TextView tvSub = new TextView(context);
        tvSub.setText(subtitle);
        tvSub.setTextSize(12);
        tvSub.setTextColor(0xFF515F74);
        tvSub.setPadding(0, dp(context, 2), 0, dp(context, 12));
        layout.addView(tvSub);
    }

    private static EditText addField(Context context, LinearLayout layout, String label, String hint, int inputType) {
        TextView tv = new TextView(context);
        tv.setText(label);
        tv.setTextColor(0xFF515F74);
        tv.setTextSize(12);
        tv.setTypeface(null, Typeface.BOLD);
        tv.setPadding(0, dp(context, 6), 0, dp(context, 2));
        layout.addView(tv);

        EditText et = new EditText(context);
        et.setHint(hint);
        et.setInputType(inputType);
        et.setTextSize(14);
        et.setTextColor(0xFF1A1C1F);
        layout.addView(et);
        return et;
    }

    public static void showPasswordResetsDialog(Context context) {
        ProgressDialog progress = ProgressDialog.show(context, "", "Fetching account recovery requests...", true);

        ApiClient.getApiService(context).getPasswordResets().enqueue(new Callback<List<PasswordResetDto>>() {
            @Override
            public void onResponse(Call<List<PasswordResetDto>> call, Response<List<PasswordResetDto>> response) {
                progress.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    List<PasswordResetDto> list = response.body();
                    displayPasswordResetsList(context, list);
                } else {
                    showError(context, "Failed to load recovery requests: " + extractError(response));
                }
            }

            @Override
            public void onFailure(Call<List<PasswordResetDto>> call, Throwable t) {
                progress.dismiss();
                showError(context, "Network error: " + t.getMessage());
            }
        });
    }

    private static void displayPasswordResetsList(Context context, List<PasswordResetDto> list) {
        if (list == null || list.isEmpty()) {
            new AlertDialog.Builder(context)
                    .setTitle("Account Recovery & IT Resets")
                    .setMessage("No pending account recovery or password reset requests at this time.")
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }

        ScrollView scrollView = new ScrollView(context);
        LinearLayout container = new LinearLayout(context);
        container.setOrientation(LinearLayout.VERTICAL);
        int pad = dp(context, 16);
        container.setPadding(pad, pad, pad, pad);
        scrollView.addView(container);

        addHeader(context, container, "Account Recovery & IT Resets",
                list.size() + " total request(s) routed to Institute IT Desk");

        for (PasswordResetDto item : list) {
            com.google.android.material.card.MaterialCardView card = new com.google.android.material.card.MaterialCardView(context);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.bottomMargin = dp(context, 14);
            card.setLayoutParams(lp);
            card.setRadius(dp(context, 12));
            card.setCardElevation(dp(context, 1));
            card.setStrokeWidth(dp(context, 1));
            card.setStrokeColor(0xFFE0E0E0);
            card.setCardBackgroundColor(0xFFFFFFFF);

            LinearLayout cardLayout = new LinearLayout(context);
            cardLayout.setOrientation(LinearLayout.VERTICAL);
            cardLayout.setPadding(dp(context, 12), dp(context, 12), dp(context, 12), dp(context, 12));
            card.addView(cardLayout);

            // Row 1: Name & Role Badge & Status
            LinearLayout row1 = new LinearLayout(context);
            row1.setOrientation(LinearLayout.HORIZONTAL);
            row1.setGravity(android.view.Gravity.CENTER_VERTICAL);

            TextView tvName = new TextView(context);
            tvName.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
            tvName.setText(item.getUserFullName() != null ? item.getUserFullName() : "User #" + item.getUserId());
            tvName.setTextSize(15);
            tvName.setTypeface(null, Typeface.BOLD);
            tvName.setTextColor(0xFF1A1C1F);
            row1.addView(tvName);

            TextView tvRole = new TextView(context);
            tvRole.setText(" " + item.getUserRole() + " ");
            tvRole.setTextSize(10);
            tvRole.setTypeface(null, Typeface.BOLD);
            tvRole.setTextColor(0xFF0F6746);
            tvRole.setBackgroundResource(R.drawable.bg_tag_rounded);
            row1.addView(tvRole);

            TextView tvStatus = new TextView(context);
            LinearLayout.LayoutParams statusLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            statusLp.setMarginStart(dp(context, 6));
            tvStatus.setLayoutParams(statusLp);
            tvStatus.setText(" " + item.getStatus() + " ");
            tvStatus.setTextSize(10);
            tvStatus.setTypeface(null, Typeface.BOLD);
            if ("APPROVED".equalsIgnoreCase(item.getStatus())) {
                tvStatus.setTextColor(0xFF0F6746);
            } else if ("REJECTED".equalsIgnoreCase(item.getStatus())) {
                tvStatus.setTextColor(0xFFBA1A1A);
            } else {
                tvStatus.setTextColor(0xFFBA5333);
            }
            tvStatus.setBackgroundResource(R.drawable.bg_tag_rounded);
            row1.addView(tvStatus);

            cardLayout.addView(row1);

            // Row 2: ID & Email
            TextView tvSub = new TextView(context);
            tvSub.setText("ID: " + (item.getInstitutionalId() != null ? item.getInstitutionalId() : "N/A") + "  ·  " + (item.getUserEmail() != null ? item.getUserEmail() : ""));
            tvSub.setTextSize(12);
            tvSub.setTextColor(0xFF515F74);
            tvSub.setPadding(0, dp(context, 3), 0, 0);
            cardLayout.addView(tvSub);

            // Row 3: Reason
            if (item.getReason() != null && !item.getReason().trim().isEmpty()) {
                TextView tvReason = new TextView(context);
                tvReason.setText("Reason: " + item.getReason());
                tvReason.setTextSize(12);
                tvReason.setTextColor(0xFF333333);
                tvReason.setPadding(0, dp(context, 4), 0, 0);
                cardLayout.addView(tvReason);
            }

            // Row 4: Assigned Handler
            String handler = item.getAssignedHandler() != null ? item.getAssignedHandler() : "Institute IT Helpdesk";
            TextView tvHandler = new TextView(context);
            tvHandler.setText("Routed to: " + handler);
            tvHandler.setTextSize(11);
            tvHandler.setTextColor(0xFF026AA7);
            tvHandler.setPadding(0, dp(context, 2), 0, dp(context, 6));
            cardLayout.addView(tvHandler);

            // Contact Phone Row with Call Button
            String phone = item.getContactPhone();
            LinearLayout phoneRow = new LinearLayout(context);
            phoneRow.setOrientation(LinearLayout.HORIZONTAL);
            phoneRow.setGravity(android.view.Gravity.CENTER_VERTICAL);
            phoneRow.setPadding(0, dp(context, 4), 0, dp(context, 8));

            TextView tvPhone = new TextView(context);
            tvPhone.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
            tvPhone.setText("📞 Phone: " + (phone != null && !phone.isEmpty() ? phone : "Not on file"));
            tvPhone.setTextSize(12);
            tvPhone.setTypeface(null, Typeface.BOLD);
            tvPhone.setTextColor(0xFF1A1C1F);
            phoneRow.addView(tvPhone);

            if (phone != null && !phone.trim().isEmpty()) {
                Button btnCall = new Button(context, null, android.R.attr.borderlessButtonStyle);
                btnCall.setText("Call Requester");
                btnCall.setTextSize(11);
                btnCall.setTextColor(0xFF0F6746);
                btnCall.setPadding(dp(context, 8), 0, dp(context, 8), 0);
                btnCall.setOnClickListener(v -> {
                    try {
                        Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone.trim()));
                        context.startActivity(dialIntent);
                    } catch (Exception e) {
                        Toast.makeText(context, "Phone: " + phone, Toast.LENGTH_SHORT).show();
                    }
                });
                phoneRow.addView(btnCall);
            }
            cardLayout.addView(phoneRow);

            // Action Buttons Row (Route, Approve, Reject) if not already approved/rejected
            if (!"APPROVED".equalsIgnoreCase(item.getStatus()) && !"REJECTED".equalsIgnoreCase(item.getStatus())) {
                LinearLayout btnRow = new LinearLayout(context);
                btnRow.setOrientation(LinearLayout.HORIZONTAL);
                btnRow.setGravity(android.view.Gravity.END);

                Button btnRoute = new Button(context, null, android.R.attr.borderlessButtonStyle);
                btnRoute.setText("🔀 Route");
                btnRoute.setTextSize(11);
                btnRoute.setTextColor(0xFF026AA7);
                btnRoute.setOnClickListener(v -> showRouteTicketDialog(context, item.getId(), () -> showPasswordResetsDialog(context)));
                btnRow.addView(btnRoute);

                Button btnReject = new Button(context, null, android.R.attr.borderlessButtonStyle);
                btnReject.setText("✕ Reject");
                btnReject.setTextSize(11);
                btnReject.setTextColor(0xFFBA1A1A);
                btnReject.setOnClickListener(v -> {
                    new AlertDialog.Builder(context)
                            .setTitle("Reject Request")
                            .setMessage("Reject account recovery request for " + item.getUserFullName() + "?")
                            .setPositiveButton("Reject", (d, w) -> {
                                ProgressDialog pd = ProgressDialog.show(context, "", "Rejecting...", true);
                                ApiClient.getApiService(context).rejectPasswordReset(item.getId()).enqueue(new Callback<Map<String, String>>() {
                                    @Override
                                    public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> res) {
                                        pd.dismiss();
                                        Toast.makeText(context, "Request rejected", Toast.LENGTH_SHORT).show();
                                        showPasswordResetsDialog(context);
                                    }

                                    @Override
                                    public void onFailure(Call<Map<String, String>> call, Throwable t) {
                                        pd.dismiss();
                                        showError(context, "Failed: " + t.getMessage());
                                    }
                                });
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                });
                btnRow.addView(btnReject);

                Button btnApprove = new Button(context, null, android.R.attr.borderlessButtonStyle);
                btnApprove.setText("✓ Approve");
                btnApprove.setTextSize(11);
                btnApprove.setTextColor(0xFF0F6746);
                btnApprove.setTypeface(null, Typeface.BOLD);
                btnApprove.setOnClickListener(v -> {
                    new AlertDialog.Builder(context)
                            .setTitle("Approve Account Recovery")
                            .setMessage("Reset password and generate temporary credentials for " + item.getUserFullName() + "?")
                            .setPositiveButton("Approve & Issue", (d, w) -> {
                                ProgressDialog pd = ProgressDialog.show(context, "", "Issuing credentials...", true);
                                ApiClient.getApiService(context).approvePasswordReset(item.getId()).enqueue(new Callback<CredentialResponse>() {
                                    @Override
                                    public void onResponse(Call<CredentialResponse> call, Response<CredentialResponse> res) {
                                        pd.dismiss();
                                        if (res.isSuccessful() && res.body() != null) {
                                            showCredentialDialog(context, "Password Reset Issued", res.body());
                                        } else {
                                            showError(context, "Approval failed: " + extractError(res));
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<CredentialResponse> call, Throwable t) {
                                        pd.dismiss();
                                        showError(context, "Network error: " + t.getMessage());
                                    }
                                });
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                });
                btnRow.addView(btnApprove);

                cardLayout.addView(btnRow);
            }

            container.addView(card);
        }

        new AlertDialog.Builder(context)
                .setView(scrollView)
                .setPositiveButton("Close", null)
                .show();
    }

    private static void showRouteTicketDialog(Context context, Long requestId, Runnable onRouted) {
        String[] handlers = new String[]{
                "Institute IT Helpdesk (Central)",
                "Campus Systems Administrator",
                "IT Infrastructure & Security Team",
                "Institute Registrar / Admin",
                "Custom Handler / Staff Member..."
        };

        new AlertDialog.Builder(context)
                .setTitle("Route / Reassign Recovery Ticket")
                .setItems(handlers, (dialog, which) -> {
                    if (which == 4) {
                        EditText etCustom = new EditText(context);
                        etCustom.setHint("Enter staff or desk name");
                        new AlertDialog.Builder(context)
                                .setTitle("Custom IT Handler")
                                .setView(etCustom)
                                .setPositiveButton("Assign", (d, w) -> {
                                    String name = etCustom.getText().toString().trim();
                                    if (!name.isEmpty()) {
                                        doAssign(context, requestId, name, onRouted);
                                    }
                                })
                                .setNegativeButton("Cancel", null)
                                .show();
                    } else {
                        doAssign(context, requestId, handlers[which], onRouted);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private static void doAssign(Context context, Long requestId, String handlerName, Runnable onRouted) {
        Map<String, String> body = new HashMap<>();
        body.put("handlerName", handlerName);
        body.put("handlerDepartment", "IT_SUPPORT");

        ProgressDialog pd = ProgressDialog.show(context, "", "Reassigning ticket...", true);
        ApiClient.getApiService(context).assignPasswordReset(requestId, body).enqueue(new Callback<PasswordResetDto>() {
            @Override
            public void onResponse(Call<PasswordResetDto> call, Response<PasswordResetDto> response) {
                pd.dismiss();
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Ticket routed to " + handlerName, Toast.LENGTH_SHORT).show();
                    if (onRouted != null) onRouted.run();
                } else {
                    showError(context, "Failed to reassign: " + extractError(response));
                }
            }

            @Override
            public void onFailure(Call<PasswordResetDto> call, Throwable t) {
                pd.dismiss();
                showError(context, "Network error: " + t.getMessage());
            }
        });
    }

    private static void showError(Context context, String message) {
        new AlertDialog.Builder(context)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    private static String extractError(Response<?> response) {
        try {
            if (response.errorBody() != null) {
                String raw = response.errorBody().string();
                org.json.JSONObject json = new org.json.JSONObject(raw);
                if (json.has("message")) return json.getString("message");
                if (json.has("error")) return json.getString("error");
                return raw;
            }
        } catch (Exception ignored) {}
        return "HTTP " + response.code();
    }

    public static void showEmergencyContactsDialog(Context context) {
        ScrollView scrollView = new ScrollView(context);
        LinearLayout layout = createFormContainer(context);
        scrollView.addView(layout);

        addHeader(context, layout, "Configure Emergency Helplines", "Set campus emergency and desk phone numbers");

        EditText etAmbulance = addField(context, layout, "Campus Ambulance / Medical Emergency *", "e.g. 98765 43210 or 108", InputType.TYPE_CLASS_PHONE);
        EditText etSecurity = addField(context, layout, "Campus Security Gate Desk *", "e.g. 91234 56780 or 112", InputType.TYPE_CLASS_PHONE);
        EditText etEmergencyDesk = addField(context, layout, "Central Duty Desk / Helpline *", "e.g. 90000 11111", InputType.TYPE_CLASS_PHONE);

        // Fetch current
        ApiClient.getApiService(context).getEmergencyContacts().enqueue(new Callback<EmergencyContactsDto>() {
            @Override
            public void onResponse(Call<EmergencyContactsDto> call, Response<EmergencyContactsDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    EmergencyContactsDto c = response.body();
                    if (c.getAmbulanceContact() != null) etAmbulance.setText(c.getAmbulanceContact());
                    if (c.getSecurityContact() != null) etSecurity.setText(c.getSecurityContact());
                    if (c.getEmergencyDeskContact() != null) etEmergencyDesk.setText(c.getEmergencyDeskContact());
                }
            }

            @Override
            public void onFailure(Call<EmergencyContactsDto> call, Throwable t) {}
        });

        new AlertDialog.Builder(context)
                .setView(scrollView)
                .setPositiveButton("Save Helplines", (dialog, which) -> {
                    String amb = etAmbulance.getText().toString().trim();
                    String sec = etSecurity.getText().toString().trim();
                    String desk = etEmergencyDesk.getText().toString().trim();

                    if (amb.isEmpty() || sec.isEmpty() || desk.isEmpty()) {
                        Toast.makeText(context, "All helpline numbers are required.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    EmergencyContactsDto dto = new EmergencyContactsDto(amb, sec, desk);
                    ProgressDialog progress = ProgressDialog.show(context, "", "Updating helplines...", true);
                    ApiClient.getApiService(context).updateEmergencyContacts(dto).enqueue(new Callback<EmergencyContactsDto>() {
                        @Override
                        public void onResponse(Call<EmergencyContactsDto> call, Response<EmergencyContactsDto> response) {
                            progress.dismiss();
                            if (response.isSuccessful()) {
                                Toast.makeText(context, "Emergency helplines updated successfully!", Toast.LENGTH_SHORT).show();
                            } else {
                                showError(context, "Failed to update helplines: " + extractError(response));
                            }
                        }

                        @Override
                        public void onFailure(Call<EmergencyContactsDto> call, Throwable t) {
                            progress.dismiss();
                            showError(context, "Network error: " + t.getMessage());
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    public static void showPostAnnouncementDialog(Context context, boolean isWarden) {
        ScrollView scrollView = new ScrollView(context);
        LinearLayout layout = createFormContainer(context);
        scrollView.addView(layout);

        String titleHeader = isWarden ? "Post Hostel Notice" : "Broadcast Campus Announcement";
        String subHeader = isWarden ? "Broadcast urgent notice to all hostel residents" : "Broadcast announcement to all campus students";
        addHeader(context, layout, titleHeader, subHeader);

        EditText etTitle = addField(context, layout, "Notice Title *", "e.g. Water Supply Maintenance", InputType.TYPE_CLASS_TEXT);
        EditText etContent = addField(context, layout, "Notice Description / Details *", "e.g. Maintenance will take place from 2 PM to 4 PM today.", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);

        TextView tvTimer = new TextView(context);
        tvTimer.setText("Display Duration / Expiry Timer *");
        tvTimer.setTextColor(0xFF515F74);
        tvTimer.setTextSize(12);
        tvTimer.setTypeface(null, Typeface.BOLD);
        tvTimer.setPadding(0, dp(context, 8), 0, dp(context, 4));
        layout.addView(tvTimer);

        Spinner spTimer = new Spinner(context);
        spTimer.setPadding(0, dp(context, 6), 0, dp(context, 10));
        layout.addView(spTimer);

        String[] timerOptions = new String[]{
                "Permanent Notice (No Timer)",
                "2 Hours (Urgent Notice)",
                "24 Hours (1 Day)",
                "3 Days",
                "7 Days (1 Week)"
        };
        Integer[] timerHours = new Integer[]{ null, 2, 24, 72, 168 };

        ArrayAdapter<String> timerAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, timerOptions);
        spTimer.setAdapter(timerAdapter);

        CheckBox cbPinned = new CheckBox(context);
        cbPinned.setText("Pin notice to top of resident notices");
        cbPinned.setTextColor(0xFF1E293B);
        cbPinned.setTextSize(13);
        cbPinned.setPadding(0, dp(context, 6), 0, dp(context, 8));
        layout.addView(cbPinned);

        new AlertDialog.Builder(context)
                .setView(scrollView)
                .setPositiveButton("Publish Notice", (dialog, which) -> {
                    String title = etTitle.getText().toString().trim();
                    String content = etContent.getText().toString().trim();

                    if (title.isEmpty() || content.isEmpty()) {
                        Toast.makeText(context, "Notice Title and Details are required.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int timerPos = spTimer.getSelectedItemPosition();
                    Integer hours = (timerPos >= 0 && timerPos < timerHours.length) ? timerHours[timerPos] : null;

                    CreateAnnouncementRequest req = new CreateAnnouncementRequest(
                            title, content, null, hours, cbPinned.isChecked()
                    );

                    ProgressDialog progress = ProgressDialog.show(context, "", "Broadcasting notice to students...", true);
                    Call<AnnouncementDto> call = isWarden
                            ? ApiClient.getApiService(context).createWardenAnnouncement(req)
                            : ApiClient.getApiService(context).createInstituteAnnouncement(req);

                    call.enqueue(new Callback<AnnouncementDto>() {
                        @Override
                        public void onResponse(Call<AnnouncementDto> call, Response<AnnouncementDto> response) {
                            progress.dismiss();
                            if (response.isSuccessful()) {
                                Toast.makeText(context, "Announcement published and students notified!", Toast.LENGTH_LONG).show();
                            } else {
                                showError(context, "Failed to publish notice: " + extractError(response));
                            }
                        }

                        @Override
                        public void onFailure(Call<AnnouncementDto> call, Throwable t) {
                            progress.dismiss();
                            showError(context, "Network error: " + t.getMessage());
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    public static void showManageNoticesDialog(Context context, boolean isWarden) {
        ProgressDialog progress = ProgressDialog.show(context, "", "Fetching bulletins and notices...", true);
        Call<List<AnnouncementDto>> call = isWarden
                ? ApiClient.getApiService(context).getAnnouncements()
                : ApiClient.getApiService(context).getInstituteAnnouncements();

        call.enqueue(new Callback<List<AnnouncementDto>>() {
            @Override
            public void onResponse(Call<List<AnnouncementDto>> call, Response<List<AnnouncementDto>> response) {
                progress.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    displayNoticesListDialog(context, response.body(), isWarden);
                } else {
                    showError(context, "Failed to load notices: " + extractError(response));
                }
            }

            @Override
            public void onFailure(Call<List<AnnouncementDto>> call, Throwable t) {
                progress.dismiss();
                showError(context, "Network error: " + t.getMessage());
            }
        });
    }

    private static void displayNoticesListDialog(Context context, List<AnnouncementDto> notices, boolean isWarden) {
        ScrollView scrollView = new ScrollView(context);
        LinearLayout layout = createFormContainer(context);
        scrollView.addView(layout);

        String title = isWarden ? "Hostel Bulletins & Notices" : "Campus Notice Management";
        String sub = isWarden ? "Manage notices for your assigned hostel" : "Full authority: Edit & Delete campus & hostel bulletins";
        addHeader(context, layout, title, sub);

        Button btnNew = new Button(context);
        btnNew.setText("➕ Post New Notice");
        btnNew.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF0F6746));
        btnNew.setTextColor(0xFFFFFFFF);
        LinearLayout.LayoutParams btnLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(context, 44));
        btnLp.topMargin = dp(context, 10);
        btnLp.bottomMargin = dp(context, 14);
        btnNew.setLayoutParams(btnLp);
        btnNew.setOnClickListener(v -> showPostAnnouncementDialog(context, isWarden));
        layout.addView(btnNew);

        if (notices.isEmpty()) {
            TextView tvEmpty = new TextView(context);
            tvEmpty.setText("No active or archived notices found.");
            tvEmpty.setTextColor(0xFF71717A);
            tvEmpty.setPadding(0, dp(context, 20), 0, dp(context, 20));
            tvEmpty.setGravity(android.view.Gravity.CENTER);
            layout.addView(tvEmpty);
        } else {
            for (AnnouncementDto n : notices) {
                com.google.android.material.card.MaterialCardView card = new com.google.android.material.card.MaterialCardView(context);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.bottomMargin = dp(context, 12);
                card.setLayoutParams(lp);
                card.setRadius(dp(context, 12));
                card.setStrokeWidth(dp(context, 1));
                card.setStrokeColor(0xFFE2E8F0);
                card.setCardBackgroundColor(0xFFFFFFFF);

                LinearLayout cardLayout = new LinearLayout(context);
                cardLayout.setOrientation(LinearLayout.VERTICAL);
                cardLayout.setPadding(dp(context, 14), dp(context, 14), dp(context, 14), dp(context, 14));
                card.addView(cardLayout);

                // Scope & Tag Row
                LinearLayout tagRow = new LinearLayout(context);
                tagRow.setOrientation(LinearLayout.HORIZONTAL);
                tagRow.setGravity(android.view.Gravity.CENTER_VERTICAL);

                boolean isCampusWide = "INSTITUTE_WIDE".equalsIgnoreCase(n.getTargetScope()) || n.getHostelId() == null;
                TextView tvScope = new TextView(context);
                tvScope.setText(isCampusWide ? " 🏛 Institute Campus-Wide " : (" 🏢 " + (n.getHostelName() != null ? n.getHostelName() : "Hostel Notice") + " "));
                tvScope.setTextSize(10);
                tvScope.setTypeface(null, Typeface.BOLD);
                tvScope.setTextColor(isCampusWide ? 0xFF4338CA : 0xFF0F6746);
                tvScope.setBackgroundResource(R.drawable.bg_tag_rounded);
                tagRow.addView(tvScope);

                if (Boolean.TRUE.equals(n.getPinned())) {
                    TextView tvPin = new TextView(context);
                    LinearLayout.LayoutParams pinLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    pinLp.setMarginStart(dp(context, 6));
                    tvPin.setLayoutParams(pinLp);
                    tvPin.setText(" 📌 Pinned ");
                    tvPin.setTextSize(10);
                    tvPin.setTypeface(null, Typeface.BOLD);
                    tvPin.setTextColor(0xFFD97706);
                    tvPin.setBackgroundResource(R.drawable.bg_tag_rounded);
                    tagRow.addView(tvPin);
                }

                if (n.getExpiresAt() != null && !n.getExpiresAt().isEmpty()) {
                    TextView tvExp = new TextView(context);
                    LinearLayout.LayoutParams expLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    expLp.setMarginStart(dp(context, 6));
                    tvExp.setLayoutParams(expLp);
                    tvExp.setText(" ⏱ Temporary ");
                    tvExp.setTextSize(10);
                    tvExp.setTextColor(0xFF64748B);
                    tvExp.setBackgroundResource(R.drawable.bg_tag_rounded);
                    tagRow.addView(tvExp);
                } else {
                    TextView tvPerm = new TextView(context);
                    LinearLayout.LayoutParams permLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    permLp.setMarginStart(dp(context, 6));
                    tvPerm.setLayoutParams(permLp);
                    tvPerm.setText(" 🔒 Permanent ");
                    tvPerm.setTextSize(10);
                    tvPerm.setTextColor(0xFF334155);
                    tvPerm.setBackgroundResource(R.drawable.bg_tag_rounded);
                    tagRow.addView(tvPerm);
                }
                cardLayout.addView(tagRow);

                // Notice Title
                TextView tvTitle = new TextView(context);
                tvTitle.setText(n.getTitle());
                tvTitle.setTextSize(15);
                tvTitle.setTypeface(null, Typeface.BOLD);
                tvTitle.setTextColor(0xFF0F172A);
                tvTitle.setPadding(0, dp(context, 6), 0, 0);
                cardLayout.addView(tvTitle);

                // Content
                TextView tvContent = new TextView(context);
                tvContent.setText(n.getContent());
                tvContent.setTextSize(13);
                tvContent.setTextColor(0xFF334155);
                tvContent.setPadding(0, dp(context, 4), 0, dp(context, 6));
                cardLayout.addView(tvContent);

                // Author & Date
                String authorInfo = (n.getAuthorName() != null ? n.getAuthorName() : "Admin") +
                        (n.getAuthorRole() != null ? " (" + n.getAuthorRole().replace('_', ' ') + ")" : "");
                TextView tvMeta = new TextView(context);
                tvMeta.setText("Posted by " + authorInfo);
                tvMeta.setTextSize(11);
                tvMeta.setTextColor(0xFF64748B);
                cardLayout.addView(tvMeta);

                // Action Buttons (Edit & Delete)
                LinearLayout btnRow = new LinearLayout(context);
                btnRow.setOrientation(LinearLayout.HORIZONTAL);
                btnRow.setGravity(android.view.Gravity.END);
                btnRow.setPadding(0, dp(context, 6), 0, 0);

                Button btnEdit = new Button(context, null, android.R.attr.borderlessButtonStyle);
                btnEdit.setText("✏️ Edit");
                btnEdit.setTextSize(12);
                btnEdit.setTextColor(0xFF026AA7);
                btnEdit.setOnClickListener(v -> {
                    // Check role scoping
                    if (isWarden && (isCampusWide || "INSTITUTE_ADMIN".equalsIgnoreCase(n.getAuthorRole()) || "SUPER_ADMIN".equalsIgnoreCase(n.getAuthorRole()))) {
                        showError(context, "Wardens cannot modify Campus-Wide Institute announcements. Contact Institute Administration.");
                        return;
                    }
                    showEditAnnouncementDialog(context, n, isWarden, () -> showManageNoticesDialog(context, isWarden));
                });
                btnRow.addView(btnEdit);

                Button btnDel = new Button(context, null, android.R.attr.borderlessButtonStyle);
                btnDel.setText("🗑 Delete");
                btnDel.setTextSize(12);
                btnDel.setTextColor(0xFFBA1A1A);
                btnDel.setOnClickListener(v -> {
                    // Check role scoping
                    if (isWarden && (isCampusWide || "INSTITUTE_ADMIN".equalsIgnoreCase(n.getAuthorRole()) || "SUPER_ADMIN".equalsIgnoreCase(n.getAuthorRole()))) {
                        showError(context, "Wardens cannot delete Campus-Wide Institute announcements. Contact Institute Administration.");
                        return;
                    }
                    confirmDeleteAnnouncement(context, n, () -> showManageNoticesDialog(context, isWarden));
                });
                btnRow.addView(btnDel);

                cardLayout.addView(btnRow);
                layout.addView(card);
            }
        }

        new AlertDialog.Builder(context)
                .setView(scrollView)
                .setPositiveButton("Done", null)
                .show();
    }

    public static void showEditAnnouncementDialog(Context context, AnnouncementDto notice, boolean isWarden, Runnable onSuccess) {
        ScrollView scrollView = new ScrollView(context);
        LinearLayout layout = createFormContainer(context);
        scrollView.addView(layout);

        addHeader(context, layout, "Edit Notice / Announcement", "Update content and visibility settings");

        EditText etTitle = addField(context, layout, "Notice Title *", "e.g. Water Supply Maintenance", InputType.TYPE_CLASS_TEXT);
        etTitle.setText(notice.getTitle());

        EditText etContent = addField(context, layout, "Notice Description / Details *", "e.g. Maintenance details...", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        etContent.setText(notice.getContent());

        TextView tvTimer = new TextView(context);
        tvTimer.setText("Display Duration / Expiry Timer *");
        tvTimer.setTextColor(0xFF515F74);
        tvTimer.setTextSize(12);
        tvTimer.setTypeface(null, Typeface.BOLD);
        tvTimer.setPadding(0, dp(context, 8), 0, dp(context, 4));
        layout.addView(tvTimer);

        Spinner spTimer = new Spinner(context);
        spTimer.setPadding(0, dp(context, 6), 0, dp(context, 10));
        layout.addView(spTimer);

        String[] timerOptions = new String[]{
                "Keep Existing Expiry / Permanent",
                "2 Hours (Urgent Notice)",
                "24 Hours (1 Day)",
                "3 Days",
                "7 Days (1 Week)"
        };
        Integer[] timerHours = new Integer[]{ null, 2, 24, 72, 168 };

        ArrayAdapter<String> timerAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, timerOptions);
        spTimer.setAdapter(timerAdapter);

        CheckBox cbPinned = new CheckBox(context);
        cbPinned.setText("Pin notice to top of resident notices");
        cbPinned.setTextColor(0xFF1E293B);
        cbPinned.setTextSize(13);
        cbPinned.setPadding(0, dp(context, 6), 0, dp(context, 8));
        cbPinned.setChecked(Boolean.TRUE.equals(notice.getPinned()));
        layout.addView(cbPinned);

        new AlertDialog.Builder(context)
                .setView(scrollView)
                .setPositiveButton("Save Changes", (dialog, which) -> {
                    String title = etTitle.getText().toString().trim();
                    String content = etContent.getText().toString().trim();

                    if (title.isEmpty() || content.isEmpty()) {
                        Toast.makeText(context, "Title and details are required.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int timerPos = spTimer.getSelectedItemPosition();
                    Integer hours = (timerPos > 0 && timerPos < timerHours.length) ? timerHours[timerPos] : null;

                    CreateAnnouncementRequest req = new CreateAnnouncementRequest(
                            title, content, notice.getHostelId(), hours, cbPinned.isChecked()
                    );

                    ProgressDialog progress = ProgressDialog.show(context, "", "Updating notice...", true);
                    ApiClient.getApiService(context).updateAnnouncement(notice.getId(), req).enqueue(new Callback<AnnouncementDto>() {
                        @Override
                        public void onResponse(Call<AnnouncementDto> call, Response<AnnouncementDto> response) {
                            progress.dismiss();
                            if (response.isSuccessful()) {
                                Toast.makeText(context, "Notice updated successfully.", Toast.LENGTH_SHORT).show();
                                if (onSuccess != null) onSuccess.run();
                            } else {
                                showError(context, "Failed to update notice: " + extractError(response));
                            }
                        }

                        @Override
                        public void onFailure(Call<AnnouncementDto> call, Throwable t) {
                            progress.dismiss();
                            showError(context, "Network error: " + t.getMessage());
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    public static void confirmDeleteAnnouncement(Context context, AnnouncementDto notice, Runnable onSuccess) {
        new AlertDialog.Builder(context)
                .setTitle("Delete Announcement")
                .setMessage("Are you sure you want to permanently delete '" + notice.getTitle() + "'?\n\nThis will immediately remove the notice for all students and staff.")
                .setPositiveButton("Delete Permanently", (dialog, which) -> {
                    ProgressDialog progress = ProgressDialog.show(context, "", "Deleting notice...", true);
                    ApiClient.getApiService(context).deleteAnnouncement(notice.getId()).enqueue(new Callback<Map<String, String>>() {
                        @Override
                        public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                            progress.dismiss();
                            if (response.isSuccessful()) {
                                Toast.makeText(context, "Notice deleted successfully.", Toast.LENGTH_SHORT).show();
                                if (onSuccess != null) onSuccess.run();
                            } else {
                                showError(context, "Failed to delete notice: " + extractError(response));
                            }
                        }

                        @Override
                        public void onFailure(Call<Map<String, String>> call, Throwable t) {
                            progress.dismiss();
                            showError(context, "Network error: " + t.getMessage());
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    public static void showChangePasswordDialog(Context context) {
        LinearLayout layout = createFormContainer(context);
        addHeader(context, layout, "Change Account Password", "Establish a new permanent facilities console password");

        EditText etCurrent = addField(context, layout, "Current Password *", "Enter your current password", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        EditText etNew = addField(context, layout, "New Password (min 6 characters) *", "Enter new password", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        EditText etConfirm = addField(context, layout, "Confirm New Password *", "Re-enter new password", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        new AlertDialog.Builder(context)
                .setView(layout)
                .setPositiveButton("Update Password", (dialog, which) -> {
                    String curr = etCurrent.getText().toString().trim();
                    String newPwd = etNew.getText().toString().trim();
                    String conf = etConfirm.getText().toString().trim();

                    if (curr.isEmpty() || newPwd.isEmpty()) {
                        Toast.makeText(context, "Current and new password are required.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (newPwd.length() < 6) {
                        Toast.makeText(context, "New password must be at least 6 characters.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!newPwd.equals(conf)) {
                        Toast.makeText(context, "New passwords do not match.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    ProgressDialog progress = ProgressDialog.show(context, "", "Updating password...", true);
                    com.adarshsingh.adminhosteldesk.data.model.ChangePasswordRequest req =
                            new com.adarshsingh.adminhosteldesk.data.model.ChangePasswordRequest(curr, newPwd);

                    ApiClient.getApiService(context).changePassword(req).enqueue(new Callback<Map<String, String>>() {
                        @Override
                        public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                            progress.dismiss();
                            if (response.isSuccessful()) {
                                Toast.makeText(context, "Password updated successfully!", Toast.LENGTH_LONG).show();
                            } else {
                                showError(context, "Failed to update password: " + extractError(response));
                            }
                        }

                        @Override
                        public void onFailure(Call<Map<String, String>> call, Throwable t) {
                            progress.dismiss();
                            showError(context, "Network error: " + t.getMessage());
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private static int dp(Context context, int dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }
}
