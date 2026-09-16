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
import com.adarshsingh.hosteldesk.data.api.ApiClient;
import com.adarshsingh.hosteldesk.data.model.NotificationDto;
import com.adarshsingh.hosteldesk.databinding.FragmentAlertsBinding;
import com.adarshsingh.hosteldesk.ui.adapter.NotificationAdapter;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlertsFragment extends Fragment {

    private FragmentAlertsBinding binding;
    private NotificationAdapter notificationAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAlertsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();

        binding.swipeRefreshAlerts.setOnRefreshListener(this::fetchNotifications);

        binding.btnMarkAllRead.setOnClickListener(v -> markAllAsRead());

        fetchNotifications();
    }

    private void setupRecyclerView() {
        notificationAdapter = new NotificationAdapter(notification -> {
            if (!notification.isRead()) {
                notification.setRead(true);
                notificationAdapter.notifyDataSetChanged();
                markSingleAsRead(notification.getId());
            }

            if (notification.getIssueId() != null) {
                Intent intent = new Intent(requireContext(), IssueDetailActivity.class);
                intent.putExtra("issue_id", notification.getIssueId());
                startActivity(intent);
            }
        });

        binding.rvNotifications.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvNotifications.setAdapter(notificationAdapter);
    }

    private void fetchNotifications() {
        binding.swipeRefreshAlerts.setRefreshing(true);

        ApiClient.getApiService(requireContext()).getNotifications()
                .enqueue(new Callback<List<NotificationDto>>() {
                    @Override
                    public void onResponse(Call<List<NotificationDto>> call, Response<List<NotificationDto>> response) {
                        if (!isAdded()) return;
                        binding.swipeRefreshAlerts.setRefreshing(false);

                        if (response.code() == 401) {
                            com.adarshsingh.hosteldesk.data.local.SessionManager.getInstance(requireContext()).clearSession();
                            Intent intent = new Intent(requireContext(), LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            return;
                        }

                        if (response.isSuccessful() && response.body() != null) {
                            List<NotificationDto> list = response.body();
                            notificationAdapter.setItems(list);

                            long unread = list.stream().filter(n -> !n.isRead()).count();
                            if (unread == 0) {
                                binding.tvAlertCount.setText("All caught up · 0 unread");
                            } else {
                                binding.tvAlertCount.setText(unread + (unread == 1 ? " unread ticket update" : " unread ticket updates"));
                            }

                            if (list.isEmpty()) {
                                binding.rvNotifications.setVisibility(View.GONE);
                                binding.layoutEmptyAlerts.setVisibility(View.VISIBLE);
                            } else {
                                binding.rvNotifications.setVisibility(View.VISIBLE);
                                binding.layoutEmptyAlerts.setVisibility(View.GONE);
                            }
                        } else {
                            Toast.makeText(requireContext(), "Failed to load notifications", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<NotificationDto>> call, Throwable t) {
                        if (!isAdded()) return;
                        binding.swipeRefreshAlerts.setRefreshing(false);
                    }
                });
    }

    private void markSingleAsRead(Long id) {
        ApiClient.getApiService(requireContext()).markNotificationAsRead(id)
                .enqueue(new Callback<Map<String, String>>() {
                    @Override
                    public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                        // Refresh to sync unread badge
                        fetchNotifications();
                    }

                    @Override
                    public void onFailure(Call<Map<String, String>> call, Throwable t) {}
                });
    }

    private void markAllAsRead() {
        if (notificationAdapter != null) {
            notificationAdapter.markAllAsRead();
        }
        binding.tvAlertCount.setText("All caught up · 0 unread");
        Toast.makeText(requireContext(), "All notifications marked as read", Toast.LENGTH_SHORT).show();

        ApiClient.getApiService(requireContext()).markAllNotificationsAsRead()
                .enqueue(new Callback<Map<String, String>>() {
                    @Override
                    public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                        // Successfully persisted on server
                    }

                    @Override
                    public void onFailure(Call<Map<String, String>> call, Throwable t) {}
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchNotifications();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
