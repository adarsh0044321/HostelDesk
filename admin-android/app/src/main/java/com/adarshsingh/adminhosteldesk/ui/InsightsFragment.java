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
import com.adarshsingh.adminhosteldesk.data.api.ApiClient;
import com.adarshsingh.adminhosteldesk.data.model.InsightDto;
import com.adarshsingh.adminhosteldesk.databinding.FragmentInsightsBinding;
import com.adarshsingh.adminhosteldesk.ui.adapter.InsightAdapter;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InsightsFragment extends Fragment {

    private FragmentInsightsBinding binding;
    private InsightAdapter insightAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentInsightsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();

        binding.swipeRefreshInsights.setOnRefreshListener(this::fetchInsights);

        fetchInsights();
    }

    private void setupRecyclerView() {
        insightAdapter = new InsightAdapter();
        binding.rvInsights.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvInsights.setAdapter(insightAdapter);
    }

    private void fetchInsights() {
        binding.swipeRefreshInsights.setRefreshing(true);

        ApiClient.getApiService(requireContext()).getInsights()
                .enqueue(new Callback<List<InsightDto>>() {
                    @Override
                    public void onResponse(Call<List<InsightDto>> call, Response<List<InsightDto>> response) {
                        if (!isAdded()) return;
                        binding.swipeRefreshInsights.setRefreshing(false);

                        if (response.code() == 401) {
                            if (!isAdded()) return;
                            Toast.makeText(requireContext(), "Session expired. Please log in again.", Toast.LENGTH_SHORT).show();
                            com.adarshsingh.adminhosteldesk.data.local.SessionManager.getInstance(requireContext()).clearSession();
                            android.content.Intent intent = new android.content.Intent(requireContext(), AdminLoginActivity.class);
                            intent.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK | android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            if (getActivity() != null) getActivity().finish();
                            return;
                        }

                        if (response.isSuccessful() && response.body() != null) {
                            List<InsightDto> list = response.body();
                            insightAdapter.setItems(list);

                            if (list.isEmpty()) {
                                binding.rvInsights.setVisibility(View.GONE);
                                binding.layoutEmptyInsights.setVisibility(View.VISIBLE);
                            } else {
                                binding.rvInsights.setVisibility(View.VISIBLE);
                                binding.layoutEmptyInsights.setVisibility(View.GONE);
                            }
                        } else {
                            Toast.makeText(requireContext(), "Failed to load AI insights", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<InsightDto>> call, Throwable t) {
                        if (!isAdded()) return;
                        binding.swipeRefreshInsights.setRefreshing(false);
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchInsights();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
