package com.example.finsmart.main_activity.budget_page.budget_create_page;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finsmart.R;

import java.util.List;

public class IncomeAdapter extends RecyclerView.Adapter<IncomeAdapter.IncomeViewHolder> {

    private List<IncomeItem> incomeItems;

    public IncomeAdapter(List<IncomeItem> incomeItems) {
        this.incomeItems = incomeItems;
    }

    @NonNull
    @Override
    public IncomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_budget_income, parent, false);
        return new IncomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IncomeViewHolder holder, int position) {
        IncomeItem item = incomeItems.get(position);
        holder.emojiTextView.setText(item.getEmoji());
        holder.titleTextView.setText(item.getTitle());
        holder.amountTextView.setText(item.getAmount());
        if (item.isLast()) {
            holder.budgetCategoryDivider.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return incomeItems.size();
    }

    static class IncomeViewHolder extends RecyclerView.ViewHolder {
        TextView emojiTextView;
        TextView titleTextView;
        TextView amountTextView;
        View budgetCategoryDivider;

        public IncomeViewHolder(@NonNull View itemView) {
            super(itemView);
            emojiTextView = itemView.findViewById(R.id.iconImageView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            amountTextView = itemView.findViewById(R.id.amountTextView);
            budgetCategoryDivider = itemView.findViewById(R.id.budgetCategoryDivider);

        }
    }
}