package com.example.finsmart.main_activity.operations_page;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finsmart.R;
import com.example.finsmart.data.model.Operation;
import com.example.finsmart.data.model.common.FlowType;
import com.example.finsmart.databinding.ItemDayHeaderBinding;
import com.example.finsmart.databinding.ItemOperationBinding;
import com.example.finsmart.main_activity.CurrencyUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class DayGroupAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_OPERATION = 1;

    private List<OperationWithDate> items = new ArrayList<>();

    public void submitList(List<OperationWithDate> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position).isHeader()) {
            return TYPE_HEADER;
        } else {
            return TYPE_OPERATION;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == TYPE_HEADER) {
            View view = inflater.inflate(R.layout.item_day_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_operation, parent, false);
            return new OperationViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        OperationWithDate item = items.get(position);

        if (holder instanceof HeaderViewHolder) {
            boolean isFirstItem = position == 0;
            ((HeaderViewHolder) holder).bind(item.getDate(), isFirstItem);
        } else if (holder instanceof OperationViewHolder) {
            ((OperationViewHolder) holder).bind(item.getOperation());
        }


    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // ViewHolder для заголовка дня
    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView dayText;
        LinearLayout headerLayout;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            dayText = itemView.findViewById(R.id.dayText);
            headerLayout = itemView.findViewById(R.id.headerLayout);
        }

        public void bind(String date, boolean isFirstItem) {
            String formattedDate = DateUtils.getDisplayDate(date);
            dayText.setText(formattedDate);

            // Получаем LayoutParams
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) headerLayout.getLayoutParams();

            if (isFirstItem) {
                params.topMargin = 0; // Убираем отступ сверху
            } else {
                params.topMargin = convertDpToPx(16, headerLayout.getContext()); // или другое значение по умолчанию
            }

            headerLayout.setLayoutParams(params);
        }

        private int convertDpToPx(int dp, Context context) {
            return Math.round(dp * context.getResources().getDisplayMetrics().density);
        }

    }



    // ViewHolder для операции
    static class OperationViewHolder extends RecyclerView.ViewHolder {
        TextView operationName;
        TextView categoryName;
        TextView operationAmount;

        public OperationViewHolder(View itemView) {
            super(itemView);
            operationName = itemView.findViewById(R.id.operationName);
            categoryName = itemView.findViewById(R.id.categoryName);
            operationAmount = itemView.findViewById(R.id.operationAmount);
        }

        public void bind(Operation op) {
            operationName.setText(op.getTitle());
            categoryName.setText(op.getCategoryName());
            String formattedSum = CurrencyUtils.formatDoubleToString(op.getSum(), 2);
            if (op.getType() == FlowType.EXPENSE) {
                formattedSum = "-" + formattedSum;
            } else {
                formattedSum = "+" + formattedSum;
            }
            operationAmount.setText(formattedSum);
        }
    }
}