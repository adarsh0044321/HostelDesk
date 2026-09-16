package com.adarshsingh.adminhosteldesk.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.adarshsingh.adminhosteldesk.R;
import com.adarshsingh.adminhosteldesk.data.api.ApiClient;
import com.adarshsingh.adminhosteldesk.data.model.IssueDetailDto;
import com.adarshsingh.adminhosteldesk.data.model.IssueDto;
import com.adarshsingh.adminhosteldesk.data.model.UpdateProgressRequest;
import com.adarshsingh.adminhosteldesk.databinding.DialogStaffResolveBinding;
import com.adarshsingh.adminhosteldesk.databinding.FragmentStaffWorkBinding;
import com.adarshsingh.adminhosteldesk.ui.adapter.AdminTicketAdapter;
import java.util.List;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StaffWorkFragment extends Fragment {

    private FragmentStaffWorkBinding binding;
    private AdminTicketAdapter workAdapter;
    private String currentFilter = "MY_WORK";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentStaffWorkBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();
        setupFilters();

        binding.swipeRefreshStaffWork.setOnRefreshListener(this::fetchStaffJobs);

        fetchStaffJobs();
    }

    private void setupRecyclerView() {
        workAdapter = new AdminTicketAdapter(new AdminTicketAdapter.OnTicketActionListener() {
            @Override
            public void onTicketClick(IssueDto ticket) {
                if (ticket != null && ticket.getId() != null) {
                    android.content.Intent intent = new android.content.Intent(requireContext(), AdminIssueDetailActivity.class);
                    intent.putExtra("issue_id", ticket.getId());
                    startActivity(intent);
                }
            }

            @Override
            public void onAssignClick(IssueDto ticket) {}

            @Override
            public void onWorkActionClick(IssueDto ticket) {
                if ("ASSIGNED".equalsIgnoreCase(ticket.getStatus())) {
                    startJob(ticket);
                } else if ("IN_PROGRESS".equalsIgnoreCase(ticket.getStatus())) {
                    promptCompleteJob(ticket);
                }
            }
        }, true);

        binding.rvStaffWork.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvStaffWork.setAdapter(workAdapter);
    }

    private void setupFilters() {
        binding.chipGroupStaffWork.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int id = checkedIds.get(0);

            if (id == R.id.chipMyWork) {
                currentFilter = "MY_WORK";
            } else if (id == R.id.chipDeptQueue) {
                currentFilter = "DEPT_QUEUE";
            } else if (id == R.id.chipCompletedJobs) {
                currentFilter = "RESOLVED_HISTORY";
            }

            fetchStaffJobs();
        });
    }

    private void fetchStaffJobs() {
        binding.swipeRefreshStaffWork.setRefreshing(true);

        ApiClient.getApiService(requireContext()).getStaffIssues(currentFilter)
                .enqueue(new Callback<List<IssueDto>>() {
                    @Override
                    public void onResponse(Call<List<IssueDto>> call, Response<List<IssueDto>> response) {
                        if (!isAdded()) return;
                        binding.swipeRefreshStaffWork.setRefreshing(false);

                        if (response.code() == 401) {
                            handleSessionExpired();
                            return;
                        }

                        if (response.isSuccessful() && response.body() != null) {
                            List<IssueDto> list = response.body();
                            workAdapter.setItems(list);

                            if (list.isEmpty()) {
                                binding.rvStaffWork.setVisibility(View.GONE);
                                binding.layoutEmptyWork.setVisibility(View.VISIBLE);
                            } else {
                                binding.rvStaffWork.setVisibility(View.VISIBLE);
                                binding.layoutEmptyWork.setVisibility(View.GONE);
                            }
                        } else {
                            Toast.makeText(requireContext(), "Failed to load assigned jobs", Toast.LENGTH_SHORT).show();
                        }
                    }

                    private void handleSessionExpired() {
                        if (!isAdded()) return;
                        Toast.makeText(requireContext(), "Session expired. Please log in again.", Toast.LENGTH_SHORT).show();
                        com.adarshsingh.adminhosteldesk.data.local.SessionManager.getInstance(requireContext()).clearSession();
                        android.content.Intent intent = new android.content.Intent(requireContext(), AdminLoginActivity.class);
                        intent.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK | android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        if (getActivity() != null) getActivity().finish();
                    }

                    @Override
                    public void onFailure(Call<List<IssueDto>> call, Throwable t) {
                        if (!isAdded()) return;
                        binding.swipeRefreshStaffWork.setRefreshing(false);
                    }
                });
    }

    private void startJob(IssueDto ticket) {
        ApiClient.getApiService(requireContext()).startWork(ticket.getId())
                .enqueue(new Callback<IssueDetailDto>() {
                    @Override
                    public void onResponse(Call<IssueDetailDto> call, Response<IssueDetailDto> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(requireContext(), "Work started on ticket #" + ticket.getTicketNumber(), Toast.LENGTH_SHORT).show();
                            fetchStaffJobs();
                        } else {
                            Toast.makeText(requireContext(), "Failed to update ticket status", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<IssueDetailDto> call, Throwable t) {
                        Toast.makeText(requireContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void promptCompleteJob(IssueDto ticket) {
        DialogStaffResolveBinding dialogBinding = DialogStaffResolveBinding.inflate(LayoutInflater.from(requireContext()));
        dialogBinding.tvResolveTicketInfo.setText("Ticket #" + (ticket.getTicketNumber() != null ? ticket.getTicketNumber() : "HD-" + ticket.getId()) +
                " · " + ticket.getTitle());

        new AlertDialog.Builder(requireContext())
                .setView(dialogBinding.getRoot())
                .setPositiveButton("Submit & Mark Fixed", (dialog, which) -> {
                    String notes = dialogBinding.etResolveNotes.getText() != null ? dialogBinding.etResolveNotes.getText().toString().trim() : "";
                    if (notes.isEmpty()) {
                        notes = "Maintenance repair completed successfully by technician.";
                    }

                    RequestBody notesPart = RequestBody.create(MediaType.parse("text/plain"), notes);

                    ApiClient.getApiService(requireContext()).completeWork(ticket.getId(), notesPart, null)
                            .enqueue(new Callback<IssueDetailDto>() {
                                @Override
                                public void onResponse(Call<IssueDetailDto> call, Response<IssueDetailDto> response) {
                                    if (response.isSuccessful()) {
                                        Toast.makeText(requireContext(), "Ticket marked fixed! Resident has been requested to verify.", Toast.LENGTH_LONG).show();
                                        fetchStaffJobs();
                                    } else {
                                        Toast.makeText(requireContext(), "Failed to complete ticket", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<IssueDetailDto> call, Throwable t) {
                                    Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void promptAddProgressNote(IssueDto ticket) {
        final EditText input = new EditText(requireContext());
        input.setHint("Add technician progress update...");
        input.setPadding(32, 24, 32, 24);

        new AlertDialog.Builder(requireContext())
                .setTitle("Update Work Progress")
                .setMessage("Log on-site progress note for Ticket #" + ticket.getTicketNumber() + ":")
                .setView(input)
                .setPositiveButton("Add Note", (dialog, which) -> {
                    String note = input.getText().toString().trim();
                    if (!note.isEmpty()) {
                        ApiClient.getApiService(requireContext()).updateProgressNote(ticket.getId(), new UpdateProgressRequest(note))
                                .enqueue(new Callback<IssueDetailDto>() {
                                    @Override
                                    public void onResponse(Call<IssueDetailDto> call, Response<IssueDetailDto> response) {
                                        if (response.isSuccessful()) {
                                            Toast.makeText(requireContext(), "Progress note recorded", Toast.LENGTH_SHORT).show();
                                            fetchStaffJobs();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<IssueDetailDto> call, Throwable t) {}
                                });
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchStaffJobs();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
