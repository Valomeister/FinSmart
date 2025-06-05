package com.example.finsmart.main_activity.home_page;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.github.mikephil.charting.data.Entry;

import java.util.List;
import java.util.Map;

public class SharedViewModel extends AndroidViewModel {
    private MutableLiveData<List<Entry>> portfolioData = new MutableLiveData<>();

    public SharedViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<Entry>> getPortfolioData() {
        return portfolioData;
    }

    public void setPortfolioData(List<Entry> data) {
        portfolioData.setValue(data);
    }
}