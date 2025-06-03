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

import java.util.List;

public class SharedBudgetViewModel extends AndroidViewModel {
    private MutableLiveData<Budget> budget = new MutableLiveData<>();
    private MutableLiveData<List<Category>> categories = new MutableLiveData<>();

    public SharedBudgetViewModel(@NonNull Application application) {
        super(application);
    }

    public void setBudget(Budget budget) {
        this.budget.setValue(budget);
    }

    public LiveData<Budget> getBudget() {
        return budget;
    }

    public void setCategories(List<Category> categories) {
        this.categories.setValue(categories);
    }

    public LiveData<List<Category>> getCategories() {
        return categories;
    }

    public LiveData<Pair<Budget, List<Category>>> getBudgetWithCategories() {
        return new MediatorLiveData<>() {{
            final Budget[] budget = new Budget[1];
            final List<Category>[] categories = new List[1];

            addSource(getBudget(), budgetValue -> {
                budget[0] = budgetValue;
                if (budget[0] != null && categories[0] != null) {
                    setValue(Pair.create(budget[0], categories[0]));
                }
            });

            addSource(getCategories(), categoriesValue -> {
                categories[0] = categoriesValue;
                if (budget[0] != null && categories[0] != null) {
                    setValue(Pair.create(budget[0], categories[0]));
                }
            });
        }};
    }
}