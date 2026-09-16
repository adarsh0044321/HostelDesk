package com.adarshsingh.hosteldesk.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.adarshsingh.hosteldesk.data.model.NotificationDto;
import com.adarshsingh.hosteldesk.databinding.ItemNotificationBinding;
import java.util.ArrayList;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    public interface OnNotificationClickListener {
        void onNotificationClick(NotificationDto notification);
    }

    private final List<NotificationDto> items = new ArrayList<>();
    private final OnNotificationClickListener listener;

    public NotificationAdapter(OnNotificationClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<NotificationDto> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    public void markAllAsRead() {
        for (NotificationDto item : items) {
            item.setRead(true);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemNotificationBinding binding = ItemNotificationBinding.inflate(
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
        private final ItemNotificationBinding binding;

        ViewHolder(ItemNotificationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(NotificationDto notification) {
            binding.tvTitle.setText(notification.getTitle());
            binding.tvMessage.setText(notification.getMessage());
            binding.tvTime.setText(formatDate(notification.getCreatedAt()));

            android.content.Context ctx = binding.getRoot().getContext();
            if (notification.isRead()) {
                binding.unreadDot.setVisibility(View.GONE);
                binding.cardNotification.setAlpha(0.72f);
                binding.cardNotification.setCardBackgroundColor(androidx.core.content.ContextCompat.getColor(ctx, com.adarshsingh.hosteldesk.R.color.colorCanvas));
                binding.cardNotification.setStrokeColor(androidx.core.content.ContextCompat.getColor(ctx, com.adarshsingh.hosteldesk.R.color.colorBorderSubtle));
                binding.tvTitle.setTypeface(null, android.graphics.Typeface.NORMAL);
                binding.tvTitle.setTextColor(androidx.core.content.ContextCompat.getColor(ctx, com.adarshsingh.hosteldesk.R.color.colorMutedText));
                binding.ivIcon.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFE5E7EB));
                binding.ivIcon.setImageTintList(android.content.res.ColorStateList.valueOf(0xFF6B7280));
            } else {
                binding.unreadDot.setVisibility(View.VISIBLE);
                binding.cardNotification.setAlpha(1.0f);
                binding.cardNotification.setCardBackgroundColor(androidx.core.content.ContextCompat.getColor(ctx, com.adarshsingh.hosteldesk.R.color.colorCard));
                binding.cardNotification.setStrokeColor(androidx.core.content.ContextCompat.getColor(ctx, com.adarshsingh.hosteldesk.R.color.colorBorder));
                binding.tvTitle.setTypeface(null, android.graphics.Typeface.BOLD);
                binding.tvTitle.setTextColor(androidx.core.content.ContextCompat.getColor(ctx, com.adarshsingh.hosteldesk.R.color.colorCharcoal));
                binding.ivIcon.setBackgroundTintList(android.content.res.ColorStateList.valueOf(androidx.core.content.ContextCompat.getColor(ctx, com.adarshsingh.hosteldesk.R.color.colorPrimaryContainer)));
                binding.ivIcon.setImageTintList(android.content.res.ColorStateList.valueOf(androidx.core.content.ContextCompat.getColor(ctx, com.adarshsingh.hosteldesk.R.color.colorPrimary)));
            }

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    listener.onNotificationClick(notification);
                }
            });
        }

        private String formatDate(String isoDate) {
            if (isoDate == null || isoDate.isEmpty()) return "";
            try {
                if (isoDate.length() >= 16) {
                    return isoDate.substring(0, 10) + " " + isoDate.substring(11, 16);
                }
                return isoDate;
            } catch (Exception e) {
                return "";
            }
        }
    }
}
