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

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {

    private List<ExpenseItem> expenseItems;

    public ExpenseAdapter(List<ExpenseItem> expenseItems) {
        this.expenseItems = expenseItems;
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_budget_expense, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        ExpenseItem item = expenseItems.get(position);
        holder.emojiTextView.setText(item.getEmoji());
        holder.titleTextView.setText(item.getTitle());
        holder.amountTextView.setText(item.getAmount());
        holder.amountTextView.setText(item.getAmount());
        if (item.isLast()) {
            holder.budgetCategoryDivider.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return expenseItems.size();
    }

    static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView emojiTextView;
        TextView titleTextView;
        TextView amountTextView;
        View budgetCategoryDivider;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            emojiTextView = itemView.findViewById(R.id.iconImageView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            amountTextView = itemView.findViewById(R.id.amountTextView);
            budgetCategoryDivider = itemView.findViewById(R.id.budgetCategoryDivider);
        }
    }
}