package com.example.finsmart.main_activity.budget_page.budget_confirm_page;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.finsmart.R;
import com.example.finsmart.data.database.AppDatabase;
import com.example.finsmart.data.model.Budget;
import com.example.finsmart.data.model.Category;
import com.example.finsmart.data.model.common.CategoryType;
import com.example.finsmart.data.repository.AppRepository;
import com.example.finsmart.main_activity.CurrencyUtils;
import com.example.finsmart.main_activity.budget_page.BudgetPageFragment;
import com.example.finsmart.main_activity.budget_page.BudgetUtils;
import com.example.finsmart.main_activity.budget_page.SharedBudgetViewModel;
import com.github.mikephil.charting.charts.PieChart;

import java.util.ArrayList;
import java.util.List;

public class BudgetConfirmFragment extends Fragment {
    View view;
    Button createBudgetFinish;
    TextView confirmBudgetIncomesSum, confirmBudgetExpensesSum, confirmBudgetDeltaTitle;

    AppRepository appRepository;
    AppDatabase appDatabase;

    Budget draftBudget;
    List<Category> draftCategories;

    SharedBudgetViewModel sharedViewModel;

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

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedBudgetViewModel.class);

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
                    appRepository.insertBudgetWithCategories(budget, categories);
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
            if (category.getType() == CategoryType.INCOME) {
                sum += category.getPlannedLimit();
            }
        }
        return sum;
    }

    int getExpenseSum(List<Category> categories) {
        int sum = 0;
        for (Category category : categories) {
            if (category.getType() == CategoryType.EXPENSE) {
                sum += category.getPlannedLimit();
            }
        }
        return sum;
    }


}