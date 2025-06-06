package com.example.finsmart.main_activity.budget_page.budget_confirm_page;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.finsmart.R;
import com.example.finsmart.data.database.AppDatabase;
import com.example.finsmart.data.model.Budget;
import com.example.finsmart.data.model.Category;
import com.example.finsmart.data.model.common.FlowType;
import com.example.finsmart.data.repository.AppRepository;
import com.example.finsmart.main_activity.budget_page.BudgetCreationViewModel;

import java.util.List;

public class BudgetConfirmFragment extends Fragment {
    View view;
    Button createBudgetFinish;
    TextView confirmBudgetIncomesSum, confirmBudgetExpensesSum, confirmBudgetDeltaTitle;

    AppRepository appRepository;
    AppDatabase appDatabase;

    Budget draftBudget;
    List<Category> draftCategories;

    BudgetCreationViewModel sharedViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_budget_confirm_page, container, false);

        appDatabase = AppDatabase.getInstance(requireContext());

        appRepository = new AppRepository(
                appDatabase.userDao(),
                appDatabase.budgetDao(),
                appDatabase.categoryDao(),
                appDatabase.operationDao(),
                appDatabase
        );

        sharedViewModel = new ViewModelProvider(requireActivity()).get(BudgetCreationViewModel.class);

        updateBudgetData();

        initUI();

        initCreateBudgetButton();


        return view;
    }

    private void initUI() {
        // Итоги расходов и доходов бюджет
        confirmBudgetIncomesSum = view.findViewById(R.id.confirmBudgetIncomesSum);
        confirmBudgetExpensesSum = view.findViewById(R.id.confirmBudgetExpensesSum);
        confirmBudgetDeltaTitle = view.findViewById(R.id.confirmBudgetDeltaTitle);

        // Кнопка "Сохранить"
        createBudgetFinish = view.findViewById(R.id.createBudgetFinish);
    }

    public void initCreateBudgetButton() {
        createBudgetFinish.setOnClickListener(v -> {
            Log.d("tmp", "awating");
            sharedViewModel.getBudgetWithCategories().observe(getViewLifecycleOwner(), budgetWithCategories -> {
                Log.d("tmp", "got it");


                Budget budget = budgetWithCategories.first;
                List<Category> categories = budgetWithCategories.second;

                // Сохраняем в БД
                new Thread(() -> {
                    long budgetId = appRepository.insertBudgetWithCategories(budget, categories);
                    appRepository.addOperationsForDay("2025-06-01", (int) budgetId);
                    appRepository.addOperationsForDay("2025-06-02", (int) budgetId);
                    appRepository.addOperationsForDay("2025-06-03", (int) budgetId);
                    appRepository.addOperationsForDay("2025-06-04", (int) budgetId);
                    appRepository.printDatabaseContent();

                    // Переключаемся обратно на UI-поток
                    requireActivity().runOnUiThread(() -> {
                        // Удаляем предыдущий стек
                        requireActivity().getSupportFragmentManager().popBackStack("BudgetPageFragmentTag", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    });

                }).start();
            });


        });
    }

    private void updateBudgetData() {


        // Получаем данные
        sharedViewModel.getBudget().observe(getViewLifecycleOwner(), budget -> {
            draftBudget = budget;
            // Показываем подтверждение
        });

        sharedViewModel.getCategories().observe(getViewLifecycleOwner(), categories -> {
            draftCategories = categories;
            // Показываем категории
            int incomesSum = getIncomeSum(categories);
            int expensesSum = getExpenseSum(categories);
            int deltaSum = incomesSum - expensesSum;

            confirmBudgetIncomesSum.setText(String.valueOf(incomesSum));
            confirmBudgetExpensesSum.setText(String.valueOf(expensesSum));
            confirmBudgetDeltaTitle.setText(String.valueOf(deltaSum));
        });

    }

    int getIncomeSum(List<Category> categories) {
        int sum = 0;
        for (Category category : categories) {
            if (category.getType() == FlowType.INCOME) {
                sum += category.getPlannedLimit();
            }
        }
        return sum;
    }

    int getExpenseSum(List<Category> categories) {
        int sum = 0;
        for (Category category : categories) {
            if (category.getType() == FlowType.EXPENSE) {
                sum += category.getPlannedLimit();
            }
        }
        return sum;
    }


}