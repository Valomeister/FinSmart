package com.example.finsmart.main_activity.budget_page.budget_create_page;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.finsmart.MyApplication;
import com.example.finsmart.R;
import com.example.finsmart.data.database.AppDatabase;
import com.example.finsmart.data.model.Budget;
import com.example.finsmart.data.model.Category;
import com.example.finsmart.data.provider.CategoryProvider;
import com.example.finsmart.data.repository.AppRepository;
import com.example.finsmart.main_activity.budget_page.BudgetCreationViewModel;
import com.example.finsmart.main_activity.budget_page.BudgetUtils;
import com.example.finsmart.main_activity.budget_page.SharedBudgetViewModelFactory;
import com.example.finsmart.main_activity.budget_page.budget_confirm_page.BudgetConfirmFragment;

import java.util.ArrayList;
import java.util.List;

public class BudgetCreateFragment extends Fragment {
    View view;
    Button createBudgetNext;


    Budget draftBudget;
    List<Category> draftIncomeCategories;
    List<Category> draftExpenseCategories;


    BudgetCategoryDataAdapter incomeAdapter;
    BudgetCategoryDataAdapter expenseAdapter;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_budget_create_page, container, false);

        loadCategoriesRecyclerView();

        initUI();

        return view;
    }

    private void initUI() {
        createBudgetNext = view.findViewById(R.id.createBudgetNext);
        createBudgetNext.setOnClickListener(v -> {
            // Получаем обновлённые категории из адаптера
            List<Category> updatedIncomes = incomeAdapter.getUpdatedCategories();
            List<Category> updatedExpenses = expenseAdapter.getUpdatedCategories();
            List<Category> updatedCategories = new ArrayList<>();
            updatedCategories.addAll(updatedIncomes);
            updatedCategories.addAll(updatedExpenses);


            BudgetCreationViewModel sharedBudgetViewModel = new ViewModelProvider(requireActivity()).get(BudgetCreationViewModel.class);


            // Сохраняем их во ViewModel
            sharedBudgetViewModel.setCategories(updatedCategories);
            sharedBudgetViewModel.setBudget(draftBudget);

            // Переход на следующий фрагмент
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new BudgetConfirmFragment())
                    .addToBackStack(null)
                    .commit();
        });
    }



    void loadCategoriesRecyclerView() {
        draftBudget = new Budget(1, BudgetUtils.getCurrentMonth());
        CategoryProvider.DefaultCategoryProvider categoryProvider =
                new CategoryProvider.DefaultCategoryProvider();
        draftIncomeCategories = categoryProvider.getIncomeCategories(0);
        draftExpenseCategories = categoryProvider.getExpenseCategories(0);

        loadIncomesRecyclerView(draftIncomeCategories);
        loadExpensesRecyclerView(draftExpenseCategories);
    }

    void loadIncomesRecyclerView(List<Category> incomeCategories) {
        // Получаем RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewIncomes);

        // Устанавливаем менеджер макета
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });

        incomeAdapter = new BudgetCategoryDataAdapter(incomeCategories);
        recyclerView.setAdapter(incomeAdapter);

    }

    void loadExpensesRecyclerView(List<Category> expenseCategories) {
        // Получаем RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewExpenses);

        // Устанавливаем менеджер макета
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });


        expenseAdapter = new BudgetCategoryDataAdapter(expenseCategories);
        recyclerView.setAdapter(expenseAdapter);
    }

}