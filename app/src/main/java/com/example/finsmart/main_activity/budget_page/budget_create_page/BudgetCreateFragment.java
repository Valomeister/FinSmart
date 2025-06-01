package com.example.finsmart.main_activity.budget_page.budget_create_page;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.finsmart.R;
import com.example.finsmart.main_activity.CurrencyUtils;
import com.example.finsmart.main_activity.budget_page.Budget;
import com.example.finsmart.main_activity.budget_page.BudgetDBHelper;
import com.example.finsmart.main_activity.budget_page.BudgetUtils;
import com.example.finsmart.main_activity.budget_page.budget_confirm_page.BudgetConfirmFragment;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class BudgetCreateFragment extends Fragment {
    View view;
    Button createBudgetNext;
//    BudgetDBHelper dbHelper;
    BudgetCategoryDataAdapter expenseAdapter;
    BudgetCategoryDataAdapter incomeAdapter;
    List<BudgetCategoryData> incomeItems;
    List<BudgetCategoryData> expenseItems;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_budget_create_page, container, false);

//        dbHelper = new BudgetDBHelper(requireContext());

        loadIncomesRecyclerView();
        loadExpensesRecyclerView();

        initUI();

        return view;
    }

    private void initUI() {
        createBudgetNext = view.findViewById(R.id.createBudgetNext);
        createBudgetNext.setOnClickListener(v -> {


            Budget budget = new Budget(BudgetUtils.getCurrentMonth());

            for (BudgetCategoryData item : incomeItems) {
                String incomeTitle = item.getTitle();
                double incomeAmount = 0;
                try {
                    incomeAmount = CurrencyUtils.parseStringToDouble(item.getAmount());
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                budget.addIncome(incomeTitle, incomeAmount);
            }

            for (BudgetCategoryData item : expenseItems) {
                String expenseTitle = item.getTitle();
                double expenseAmount = 0;
                try {
                    expenseAmount = CurrencyUtils.parseStringToDouble(item.getAmount());
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                budget.addExpense(expenseTitle, expenseAmount);
            }

            BudgetConfirmFragment resultFragment = new BudgetConfirmFragment();
            Bundle args = new Bundle();
            args.putParcelable("budget", budget);
            resultFragment.setArguments(args);

            // Переключаемся на BudgetConfirmFragment
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, resultFragment) // заменяем текущий фрагмент
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
        incomeItems = new ArrayList<>();
        incomeItems.add(new BudgetCategoryData("\uD83D\uDCBC", "Зарплата",
                "65 000₽", false));
        incomeItems.add(new BudgetCategoryData("\uD83D\uDDA5\uFE0F", "Фриланс",
                "10 000₽", false));
        incomeItems.add(new BudgetCategoryData("\uD83D\uDCC8", "Вклады и облигации",
                "9 350₽", false));
        incomeItems.add(new BudgetCategoryData("\uD83E\uDDE9", "Другое",
                "5 000₽", true));

        // Создаем адаптер и устанавливаем его
        incomeAdapter = new BudgetCategoryDataAdapter(incomeItems);
        recyclerView.setAdapter(incomeAdapter);
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
        expenseItems = new ArrayList<>();
        expenseItems.add(new BudgetCategoryData("\uD83C\uDFE0", "Аренда",
                "65 000₽", false));
        expenseItems.add(new BudgetCategoryData("\uD83E\uDD69", "Питание",
                "10 000₽", false));
        expenseItems.add(new BudgetCategoryData("\uD83C\uDF89", "Развлечения",
                "9 350₽", false));
        expenseItems.add(new BudgetCategoryData("\uD83D\uDC55", "Одежда",
                "8 000₽", false));
        expenseItems.add(new BudgetCategoryData("\uD83D\uDE97", "Транспорт",
                "7 500₽", false));
        expenseItems.add(new BudgetCategoryData("\uD83D\uDCDE", "Связь",
                "2 000₽", true));

        // Создаем адаптер и устанавливаем его
        expenseAdapter = new BudgetCategoryDataAdapter(expenseItems);
        recyclerView.setAdapter(expenseAdapter);
    }

}