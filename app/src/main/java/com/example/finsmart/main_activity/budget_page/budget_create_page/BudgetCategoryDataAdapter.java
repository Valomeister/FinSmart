package com.example.finsmart.main_activity.budget_page.budget_create_page;

import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finsmart.R;

import java.util.List;

public class BudgetCategoryDataAdapter extends RecyclerView.Adapter<BudgetCategoryDataAdapter.budgetCategoryInfoViewHolder> {

    private List<BudgetCategoryData> budgetCategoryInfoItems;

    public BudgetCategoryDataAdapter(List<BudgetCategoryData> budgetCategoryInfoItems) {
        this.budgetCategoryInfoItems = budgetCategoryInfoItems;
    }

    @NonNull
    @Override
    public budgetCategoryInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_budget_category_info, parent, false);
        return new budgetCategoryInfoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull budgetCategoryInfoViewHolder holder, int position) {
        BudgetCategoryData item = budgetCategoryInfoItems.get(position);
        holder.emojiTextView.setText(item.getEmoji());
        holder.titleTextView.setText(item.getTitle());
        holder.amountTextView.setText(item.getAmount());

        // Установка слушателя текста
        holder.amountTextView.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // не используется
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // не используется
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Получаем позицию элемента
                int position = holder.getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    budgetCategoryInfoItems.get(position).setAmount(s.toString());
                }
            }
        });

        if (item.isLast()) {
            holder.budgetCategoryDivider.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return budgetCategoryInfoItems.size();
    }

    public List<BudgetCategoryData> getItems() {
        return budgetCategoryInfoItems;
    }



    static class budgetCategoryInfoViewHolder extends RecyclerView.ViewHolder {
        TextView emojiTextView;
        TextView titleTextView;
        EditText amountTextView;
        View budgetCategoryDivider;

        public budgetCategoryInfoViewHolder(@NonNull View itemView) {
            super(itemView);
            emojiTextView = itemView.findViewById(R.id.iconImageView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            amountTextView = itemView.findViewById(R.id.amountTextView);
            budgetCategoryDivider = itemView.findViewById(R.id.budgetCategoryDivider);
        }


    }
}