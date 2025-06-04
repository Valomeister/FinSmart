package com.example.finsmart.main_activity.budget_page;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.finsmart.data.repository.AppRepository;
import com.example.finsmart.data.database.AppDatabase;

public class SharedBudgetViewModelFactory implements ViewModelProvider.Factory {

    private final Application application;
    private final AppRepository repository;

    public SharedBudgetViewModelFactory(Application application, AppRepository repository) {
        this.application = application;
        this.repository = repository;
    }



    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(SharedBudgetViewModel.class)) {
            return (T) new SharedBudgetViewModel(application, repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}