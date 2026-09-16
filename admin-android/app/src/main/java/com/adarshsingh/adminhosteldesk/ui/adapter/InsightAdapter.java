package com.adarshsingh.adminhosteldesk.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.adarshsingh.adminhosteldesk.data.model.InsightDto;
import com.adarshsingh.adminhosteldesk.databinding.ItemInsightCardBinding;
import java.util.ArrayList;
import java.util.List;

public class InsightAdapter extends RecyclerView.Adapter<InsightAdapter.ViewHolder> {

    private final List<InsightDto> items = new ArrayList<>();

    public void setItems(List<InsightDto> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemInsightCardBinding binding = ItemInsightCardBinding.inflate(
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

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemInsightCardBinding binding;

        ViewHolder(ItemInsightCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(InsightDto insight) {
            String cat = insight.getCategory() != null ? insight.getCategory().toUpperCase() : "FACILITY_CLUSTER";
            binding.tvClusterTag.setText(cat + "_RECURRING");
            binding.tvInsightTitle.setText(insight.getPatternDescription() != null ? insight.getPatternDescription() : "Recurring Pattern Identified");

            String affected = "Category: " + (insight.getCategory() != null ? insight.getCategory() : "Facilities") +
                    (insight.getBlockName() != null ? " · " + insight.getBlockName() : "") +
                    (insight.getComplaintCount() != null ? " (" + insight.getComplaintCount() + " repeated reports)" : "");
            binding.tvAffectedUnits.setText(affected);

            binding.tvRootCause.setText(insight.getProbableCause() != null ? insight.getProbableCause() : "Analysis indicates localized system stress.");
            binding.tvRecommendation.setText(insight.getRecommendedAction() != null ? insight.getRecommendedAction() : "Schedule preventive maintenance inspection.");
        }
    }
}
