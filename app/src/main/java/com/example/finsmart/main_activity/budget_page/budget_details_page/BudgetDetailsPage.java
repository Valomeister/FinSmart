package com.example.finsmart.main_activity.budget_page.budget_details_page;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.finsmart.MyApplication;
import com.example.finsmart.R;
import com.example.finsmart.main_activity.budget_page.BudgetUtils;
import com.example.finsmart.main_activity.budget_page.SharedBudgetViewModel;
import com.example.finsmart.main_activity.budget_page.SharedBudgetViewModelFactory;

public class BudgetDetailsPage extends Fragment {

    View view;
    SharedBudgetViewModel viewModel;
    TextView budgetDetailsHeader;
    Button BudgetDetailsIncomes;
    Button BudgetDetailsExpenses;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_budget_details_page, container, false);

        MyApplication app = (MyApplication) requireActivity().getApplication();

        SharedBudgetViewModelFactory factory = new SharedBudgetViewModelFactory(
                requireActivity().getApplication(),
                app.repository
        );

        viewModel = new ViewModelProvider(this, factory).get(SharedBudgetViewModel.class);

//        viewModel.printDatabaseContent();

        initUI();

        loadIncomesDetails();

        return view;
    }

    private void initUI() {
        budgetDetailsHeader = view.findViewById(R.id.budgetDetailsHeader);
        BudgetDetailsIncomes = view.findViewById(R.id.BudgetDetailsIncomes);
        BudgetDetailsExpenses = view.findViewById(R.id.BudgetDetailsExpenses);

        BudgetDetailsIncomes.setOnClickListener(view -> {
            loadIncomesDetails();
        });

        BudgetDetailsExpenses.setOnClickListener(view -> {
            loadExpensesDetails();
        });
    }

    private void loadExpensesDetails() {
        budgetDetailsHeader.setText("Сводка расходов");

        // Инициализируем RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        ExpenseAdapter adapter = new ExpenseAdapter();
        recyclerView.setAdapter(adapter);

        // Загружаем данные
        viewModel.getBudgetByMonth(BudgetUtils.getCurrentMonth()).observe(getViewLifecycleOwner(), budget -> {
            int budgetId = budget.getBudgetId();
            viewModel.getExpenseCategoriesWithTotal(budgetId).observe(getViewLifecycleOwner(), expenseWithTotals -> {
                adapter.submitList(expenseWithTotals);
            });
        });

    }

    private void loadIncomesDetails() {
        budgetDetailsHeader.setText("Сводка Доходов");

        // Инициализируем RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        IncomeAdapter adapter = new IncomeAdapter();
        recyclerView.setAdapter(adapter);

        // Загружаем данные
        viewModel.getBudgetByMonth(BudgetUtils.getCurrentMonth()).observe(getViewLifecycleOwner(), budget -> {
            int budgetId = budget.getBudgetId();
            viewModel.getIncomeCategoriesWithTotal(budgetId).observe(getViewLifecycleOwner(), incomeWithTotals -> {
                adapter.submitList(incomeWithTotals);
            });
        });

    }


}