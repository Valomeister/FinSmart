package com.example.finsmart.main_activity.budget_page.budget_create_page;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finsmart.R;
import com.example.finsmart.data.model.Category;

import java.util.List;

public class BudgetCategoryDataAdapter extends RecyclerView.Adapter<BudgetCategoryDataAdapter.CategoryViewHolder> {

    private List<Category> categories;

    public BudgetCategoryDataAdapter(List<Category> categories) {
        this.categories = categories;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_budget_category_info, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categories.get(position);

        // Отображаем данные
        holder.emojiTextView.setText(category.getEmoji());
        holder.titleTextView.setText(category.getName());

        // Форматируем лимит: 65000 → "65 000₽"
        String formattedLimit = String.format("%,d₽", category.getPlannedLimit()).replace(',', ' ');
        holder.amountEditText.setText(formattedLimit);

        // Обработчик изменения суммы
        holder.amountEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    // Убираем всё, кроме цифр, и конвертируем в int
                    int newLimit = Integer.parseInt(s.toString().replaceAll("[^\\d]", ""));
                    category.setPlannedLimit(newLimit);
                } catch (NumberFormatException ignored) {}
            }
        });

        // Скрываем разделитель у последнего элемента
        holder.divider.setVisibility(holder.getAdapterPosition() == getItemCount() - 1 ? View.GONE : View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    // Обновление данных извне
    public void updateCategories(List<Category> newCategories) {
        this.categories = newCategories;
        notifyDataSetChanged();
    }

    public List<Category> getUpdatedCategories() {
        return categories;
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView emojiTextView;
        TextView titleTextView;
        EditText amountEditText;
        View divider;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            emojiTextView = itemView.findViewById(R.id.iconImageView); // должно быть TextView для эмодзи
            titleTextView = itemView.findViewById(R.id.titleTextView);
            amountEditText = itemView.findViewById(R.id.amountTextView);
            divider = itemView.findViewById(R.id.budgetCategoryDivider);
        }
    }
}