package com.example.finsmart;

import android.app.Application;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.finsmart.data.database.AppDatabase;
import com.example.finsmart.data.model.Budget;
import com.example.finsmart.data.repository.AppRepository;
import com.example.finsmart.main_activity.budget_page.BudgetUtils;

public class MyApplication extends Application {
    public AppRepository repository;

    @Override
    public void onCreate() {
        Log.d("tmp", "app created");
        super.onCreate();

        // Получаем базу данных
        AppDatabase database = AppDatabase.getInstance(this);

        // Создаём репозиторий один раз
        repository = new AppRepository(
                database.userDao(),
                database.budgetDao(),
                database.categoryDao(),
                database.operationDao(),
                database
        );

        repository.createDefaultBudget(BudgetUtils.getCurrentMonth());

        // вызываем данные 1 раз для кэширования
        LiveData<Budget> budgetLiveData = repository.getBudgetByMonth(BudgetUtils.getCurrentMonth());
        budgetLiveData.observeForever(budget -> {
            if (budget != null) {
                int budgetId = budget.getBudgetId();
                // вызываем данные 1 раз для кэширования
                repository.getExpenseCategoriesByBudget(budgetId);
            } else {

            }
        });


    }
}