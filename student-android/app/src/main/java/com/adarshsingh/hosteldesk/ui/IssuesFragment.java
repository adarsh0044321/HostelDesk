package com.adarshsingh.hosteldesk.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.adarshsingh.hosteldesk.R;
import com.adarshsingh.hosteldesk.data.api.ApiClient;
import com.adarshsingh.hosteldesk.data.model.IssueDto;
import com.adarshsingh.hosteldesk.databinding.FragmentIssuesBinding;
import com.adarshsingh.hosteldesk.ui.adapter.IssueAdapter;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IssuesFragment extends Fragment {

    private FragmentIssuesBinding binding;
    private IssueAdapter issueAdapter;
    private String currentStatusFilter = null;
    private boolean isFirstLoad = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentIssuesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();
        setupFilters();

        binding.swipeRefresh.setOnRefreshListener(this::fetchIssues);
        binding.btnRetry.setOnClickListener(v -> fetchIssues());
        binding.fabNewIssue.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), ReportIssueActivity.class));
        });

        isFirstLoad = false;
        fetchIssues();
    }

    private void setupRecyclerView() {
        issueAdapter = new IssueAdapter(issue -> {
            Intent intent = new Intent(requireContext(), IssueDetailActivity.class);
            intent.putExtra("issue_id", issue.getId());
            startActivity(intent);
        });
        binding.rvIssues.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvIssues.setAdapter(issueAdapter);
    }

    private void setupFilters() {
        binding.chipGroupFilter.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int checkedId = checkedIds.get(0);

            if (checkedId == R.id.chipAll) {
                currentStatusFilter = null;
            } else if (checkedId == R.id.chipOpen) {
                currentStatusFilter = "OPEN";
            } else if (checkedId == R.id.chipInProgress) {
                currentStatusFilter = "IN_PROGRESS";
            } else if (checkedId == R.id.chipResolved) {
                currentStatusFilter = "AWAITING_VERIFICATION";
            } else if (checkedId == R.id.chipClosed) {
                currentStatusFilter = "CLOSED";
            }

            fetchIssues();
        });
    }

    private void fetchIssues() {
        if (binding == null) return;

        // If list is currently empty, show prominent loading state
        if (issueAdapter.getItemCount() == 0) {
            binding.layoutLoadingState.setVisibility(View.VISIBLE);
            binding.layoutErrorState.setVisibility(View.GONE);
            binding.layoutEmptyState.setVisibility(View.GONE);
            binding.rvIssues.setVisibility(View.GONE);
        } else {
            binding.swipeRefresh.setRefreshing(true);
        }

        ApiClient.getApiService(requireContext()).getMyIssues(currentStatusFilter)
                .enqueue(new Callback<List<IssueDto>>() {
                    @Override
                    public void onResponse(Call<List<IssueDto>> call, Response<List<IssueDto>> response) {
                        if (!isAdded() || binding == null) return;
                        binding.swipeRefresh.setRefreshing(false);
                        binding.layoutLoadingState.setVisibility(View.GONE);

                        if (response.code() == 401) {
                            Toast.makeText(requireContext(), "Session expired. Please sign in again.", Toast.LENGTH_LONG).show();
                            com.adarshsingh.hosteldesk.data.local.SessionManager.getInstance(requireContext()).clearSession();
                            Intent intent = new Intent(requireContext(), LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            return;
                        }

                        if (response.isSuccessful() && response.body() != null) {
                            List<IssueDto> list = response.body();
                            issueAdapter.setItems(list);
                            binding.layoutErrorState.setVisibility(View.GONE);

                            if (list.isEmpty()) {
                                binding.rvIssues.setVisibility(View.GONE);
                                binding.layoutEmptyState.setVisibility(View.VISIBLE);
                            } else {
                                binding.rvIssues.setVisibility(View.VISIBLE);
                                binding.layoutEmptyState.setVisibility(View.GONE);
                            }
                        } else {
                            if (issueAdapter.getItemCount() == 0) {
                                binding.rvIssues.setVisibility(View.GONE);
                                binding.layoutEmptyState.setVisibility(View.GONE);
                                binding.layoutErrorState.setVisibility(View.VISIBLE);
                                binding.tvErrorMessage.setText("Server returned status " + response.code() + ". Please retry.");
                            } else {
                                Toast.makeText(requireContext(), "Failed to refresh issues (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<IssueDto>> call, Throwable t) {
                        if (!isAdded() || binding == null) return;
                        binding.swipeRefresh.setRefreshing(false);
                        binding.layoutLoadingState.setVisibility(View.GONE);

                        if (issueAdapter.getItemCount() == 0) {
                            binding.rvIssues.setVisibility(View.GONE);
                            binding.layoutEmptyState.setVisibility(View.GONE);
                            binding.layoutErrorState.setVisibility(View.VISIBLE);
                            binding.tvErrorMessage.setText(t.getMessage() != null ? t.getMessage() : "Network connection error. Please retry.");
                        } else {
                            Toast.makeText(requireContext(), "Error fetching issues: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isFirstLoad) {
            fetchIssues();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
