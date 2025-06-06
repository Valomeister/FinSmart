package com.example.finsmart.main_activity.budget_page;

import android.app.Application;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.finsmart.data.model.Budget;
import com.example.finsmart.data.model.Category;
import com.example.finsmart.data.model.Operation;
import com.example.finsmart.data.model.common.FlowType;
import com.example.finsmart.data.repository.AppRepository;
import com.example.finsmart.main_activity.budget_page.budget_details_page.CategoryWithTotal;

import java.util.List;

public class SharedBudgetViewModel extends AndroidViewModel {
    private final AppRepository repository;

    public SharedBudgetViewModel(@NonNull Application application, AppRepository repository) {
        super(application);
        this.repository = repository;
    }

    public LiveData<Budget> getBudgetByMonth(String month) {
        return repository.getBudgetByMonth(month);
    }

    public LiveData<List<Category>> getIncomeCategoriesByBudget(int budgetId) {
        return repository.getIncomeCategoriesByBudget(budgetId);
    }

    public LiveData<List<Category>> getExpenseCategoriesByBudget(int budgetId) {
        return repository.getExpenseCategoriesByBudget(budgetId);
    }

    public LiveData<List<Operation>> getOperationsByCategory(int categoryId) {
        return repository.getOperationsByCategory(categoryId);
    }

    public LiveData<List<CategoryWithTotal>> getExpenseCategoriesWithTotal(int budgetId) {
        return repository.getCategoriesWithTotalByType(budgetId, FlowType.EXPENSE);
    }

    public LiveData<List<CategoryWithTotal>> getIncomeCategoriesWithTotal(int budgetId) {
        return repository.getCategoriesWithTotalByType(budgetId, FlowType.INCOME);

    }

//    public LiveData<List<OperationWithDate>> getGroupedOperationsByDay(int budgetId) {
//        return repository.getGroupedOperationsByDay(budgetId);
//    }

    public LiveData<List<Operation>> getOperationsByBudget(int budgetId) {
        return repository.getOperationsByBudget(budgetId);
    }

    public void printDatabaseContent() {
        repository.printDatabaseContent();
    }

    public void createDefaultBudget() {
        repository.createDefaultBudget(BudgetUtils.getCurrentMonth());
    }





    public void removeDataFromDatabase() {
        repository.removeDataFromDatabase();
    }
}