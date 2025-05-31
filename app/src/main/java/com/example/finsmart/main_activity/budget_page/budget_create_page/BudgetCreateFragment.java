package com.example.finsmart.main_activity.budget_page.budget_create_page;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.finsmart.R;
import com.example.finsmart.main_activity.budget_page.budget_confirm_page.BudgetConfirmFragment;
import com.github.mikephil.charting.charts.PieChart;

import java.util.ArrayList;
import java.util.List;

public class BudgetCreateFragment extends Fragment {
    View view;
    Button createBudgetNext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_budget_create_page, container, false);

        loadIncomesRecyclerView();
        loadExpensesRecyclerView();

        initUI();

        return view;
    }

    private void initUI() {
        createBudgetNext = view.findViewById(R.id.createBudgetNext);
        createBudgetNext.setOnClickListener(v -> {
            // Переключаемся на BudgetConfirmFragment
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new BudgetConfirmFragment()) // заменяем текущий фрагмент
                    .addToBackStack(null)  // добавляем в back stack (чтобы можно было вернуться)
                    .commit();
        });
    }

    void loadIncomesRecyclerView() {
        // Получаем RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewIncomes);

        // Устанавливаем менеджер макета
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });

        // Данные для списка
        List<IncomeItem> incomeItems = new ArrayList<>();
        incomeItems.add(new IncomeItem("\uD83D\uDCBC", "Зарплата",
                "65 000₽", false));
        incomeItems.add(new IncomeItem("\uD83D\uDDA5\uFE0F", "Фриланс",
                "10 000₽", false));
        incomeItems.add(new IncomeItem("\uD83D\uDCC8", "Вклады и облигации",
                "9 350₽", false));
        incomeItems.add(new IncomeItem("\uD83E\uDDE9", "Другое",
                "5 000₽", true));

        // Создаем адаптер и устанавливаем его
        IncomeAdapter adapter = new IncomeAdapter(incomeItems);
        recyclerView.setAdapter(adapter);
    }

    void loadExpensesRecyclerView() {
        // Получаем RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewExpenses);

        // Устанавливаем менеджер макета
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });

        // Данные для списка
        List<ExpenseItem> expenseItems = new ArrayList<>();
        expenseItems.add(new ExpenseItem("\uD83C\uDFE0", "Аренда",
                "65 000₽", false));
        expenseItems.add(new ExpenseItem("\uD83E\uDD69", "Питание",
                "10 000₽", false));
        expenseItems.add(new ExpenseItem("\uD83C\uDF89", "Развлечения",
                "9 350₽", false));
        expenseItems.add(new ExpenseItem("\uD83D\uDC55", "Одежда",
                "8 000₽", false));
        expenseItems.add(new ExpenseItem("\uD83D\uDE97", "Транспорт",
                "7 500₽", false));
        expenseItems.add(new ExpenseItem("\uD83D\uDCDE", "Связь",
                "2 000₽", true));

        // Создаем адаптер и устанавливаем его
        ExpenseAdapter adapter = new ExpenseAdapter(expenseItems);
        recyclerView.setAdapter(adapter);
    }

}