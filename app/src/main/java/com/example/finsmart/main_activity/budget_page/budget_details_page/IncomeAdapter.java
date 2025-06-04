package com.example.finsmart.main_activity.budget_page.budget_details_page;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finsmart.R;

import java.util.Collections;
import java.util.List;

public class IncomeAdapter extends RecyclerView.Adapter<IncomeAdapter.IncomeViewHolder> {

    private List<CategoryWithTotal> items = Collections.emptyList();

    public void submitList(List<CategoryWithTotal> items) {
        this.items = items != null ? items : Collections.emptyList();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public IncomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_budget_details, parent, false);
        return new IncomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IncomeViewHolder holder, int position) {
        CategoryWithTotal item = items.get(position);

        holder.titleTextView.setText(item.category.getName());
        holder.amountPlanned.setText(String.valueOf(item.category.getPlannedLimit()));
        holder.amountActual.setText(String.valueOf(item.totalAmount));

        // установка соотв. цвета
        if ((double) item.totalAmount / item.category.getPlannedLimit() < 0.85) {
            holder.progressBar.setBackgroundColor(Color.parseColor("#E2474A"));
        } else if ((double) item.totalAmount / item.category.getPlannedLimit() < 1.00) {
            holder.progressBar.setBackgroundColor(Color.parseColor("#E28126"));
        } else {
            holder.progressBar.setBackgroundColor(Color.parseColor("#59C736"));
        }

        // Установка layout_weight программно
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)
                holder.progressBar.getLayoutParams();

        params.weight = Math.min(
                1, (float) item.totalAmount / item.category.getPlannedLimit());

        holder.progressBar.setLayoutParams(params);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class IncomeViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, amountPlanned, amountActual;
        View progressBar;

        IncomeViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            amountPlanned = itemView.findViewById(R.id.amountPlanned);
            amountActual = itemView.findViewById(R.id.amountActual);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }
}