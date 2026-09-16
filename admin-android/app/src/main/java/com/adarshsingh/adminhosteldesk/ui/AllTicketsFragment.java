package com.adarshsingh.adminhosteldesk.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.adarshsingh.adminhosteldesk.R;
import com.adarshsingh.adminhosteldesk.data.api.ApiClient;
import com.adarshsingh.adminhosteldesk.data.model.IssueDto;
import com.adarshsingh.adminhosteldesk.databinding.FragmentAdminTicketsBinding;
import com.adarshsingh.adminhosteldesk.ui.adapter.AdminTicketAdapter;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllTicketsFragment extends Fragment {

    private FragmentAdminTicketsBinding binding;
    private AdminTicketAdapter ticketAdapter;
    private String currentStatusFilter = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAdminTicketsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();
        setupFilters();

        binding.swipeRefreshTickets.setOnRefreshListener(this::fetchTickets);

        fetchTickets();
    }

    private void setupRecyclerView() {
        ticketAdapter = new AdminTicketAdapter(new AdminTicketAdapter.OnTicketActionListener() {
            @Override
            public void onTicketClick(IssueDto ticket) {
                if (ticket != null && ticket.getId() != null) {
                    android.content.Intent intent = new android.content.Intent(requireContext(), AdminIssueDetailActivity.class);
                    intent.putExtra("issue_id", ticket.getId());
                    startActivity(intent);
                }
            }

            @Override
            public void onAssignClick(IssueDto ticket) {
                AssignDialogHelper.showAssignDialog(requireContext(), ticket, () -> fetchTickets());
            }

            @Override
            public void onWorkActionClick(IssueDto ticket) {
                if (ticket != null && ticket.getId() != null) {
                    android.content.Intent intent = new android.content.Intent(requireContext(), AdminIssueDetailActivity.class);
                    intent.putExtra("issue_id", ticket.getId());
                    startActivity(intent);
                }
            }
        }, false);

        binding.rvAllTickets.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvAllTickets.setAdapter(ticketAdapter);
    }

    private void setupFilters() {
        binding.chipGroupAdminFilter.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int id = checkedIds.get(0);

            if (id == R.id.chipFilterAll) {
                currentStatusFilter = null;
            } else if (id == R.id.chipFilterSubmitted) {
                currentStatusFilter = "SUBMITTED";
            } else if (id == R.id.chipFilterAssigned) {
                currentStatusFilter = "ASSIGNED";
            } else if (id == R.id.chipFilterInProgress) {
                currentStatusFilter = "IN_PROGRESS";
            } else if (id == R.id.chipFilterResolved) {
                currentStatusFilter = "RESOLVED";
            } else if (id == R.id.chipFilterClosed) {
                currentStatusFilter = "CLOSED";
            }

            fetchTickets();
        });
    }

    private void fetchTickets() {
        binding.swipeRefreshTickets.setRefreshing(true);

        ApiClient.getApiService(requireContext()).getAdminIssues(currentStatusFilter, null, null)
                .enqueue(new Callback<List<IssueDto>>() {
                    @Override
                    public void onResponse(Call<List<IssueDto>> call, Response<List<IssueDto>> response) {
                        if (!isAdded()) return;
                        binding.swipeRefreshTickets.setRefreshing(false);

                        if (response.code() == 401) {
                            handleSessionExpired();
                            return;
                        }

                        if (response.isSuccessful() && response.body() != null) {
                            List<IssueDto> list = response.body();
                            ticketAdapter.setItems(list);

                            if (list.isEmpty()) {
                                binding.rvAllTickets.setVisibility(View.GONE);
                                binding.layoutEmptyTickets.setVisibility(View.VISIBLE);
                            } else {
                                binding.rvAllTickets.setVisibility(View.VISIBLE);
                                binding.layoutEmptyTickets.setVisibility(View.GONE);
                            }
                        } else {
                            Toast.makeText(requireContext(), "Failed to load tickets (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<IssueDto>> call, Throwable t) {
                        if (!isAdded()) return;
                        binding.swipeRefreshTickets.setRefreshing(false);
                        Toast.makeText(requireContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
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
    public void onResume() {
        super.onResume();
        fetchTickets();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
