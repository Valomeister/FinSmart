package com.example.finsmart.main_activity.budget_page;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.finsmart.R;
import com.example.finsmart.main_activity.budget_page.budget_create_page.BudgetCreateFragment;
import com.example.finsmart.main_activity.budget_page.budget_create_page.IncomeAdapter;
import com.example.finsmart.main_activity.budget_page.budget_create_page.IncomeItem;
import com.example.finsmart.main_activity.budget_page.budget_details_page.BudgetDetailsPage;
import com.github.mikephil.charting.charts.PieChart;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class BudgetPageFragment extends Fragment {
    View view;
    BudgetDBHelper dbHelper;

    ImageView createBudgetIllustration;
    PieChart BudgetPieChart;
    Button budgetActionButton;
    TextView budgetDescriptionTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_budget_page, container, false);

        createBudgetIllustration = view.findViewById(R.id.createBudgetIllustration);
        budgetDescriptionTextView = view.findViewById(R.id.budgetDescription);
        BudgetPieChart = view.findViewById(R.id.BudgetPieChart);
        budgetActionButton = view.findViewById(R.id.budgetActionButton);


        dbHelper = new BudgetDBHelper(requireContext());
        dbHelper.clearAllData();
//        dbHelper.populateInitialData();
        dbHelper.printAllData();

        loadBudgetSection();

        return view;
    }

    void loadBudgetSection() {
        String currentMonth = getCurrentMonth();
        if (dbHelper.getBudgetByMonth(currentMonth) == null)  {
            // Бюджета на текущий месяц нет
            loadCreateBudgetSection(currentMonth);
        }
        else {
            // Бюджет на текущий месяц есть
            loadExistingBudgetInfo(currentMonth);
        }


    }

    String getCurrentMonth() {
        // Получаем текущую дату
        LocalDate currentDate = LocalDate.now();

        // Создаём форматтер для нужного вида: MM/yyyy
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yyyy");

        // Форматируем дату
        String formattedDate = currentDate.format(formatter);

        return formattedDate;
    }

    void loadCreateBudgetSection(String month) {
        createBudgetIllustration.setVisibility(View.VISIBLE);
        budgetDescriptionTextView.setVisibility(View.VISIBLE);
        BudgetPieChart.setVisibility(View.GONE);
        budgetActionButton.setText("Создать бюджет");

        budgetActionButton.setOnClickListener(v -> {
            // Переключаемся на BudgetCreateFragment
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new BudgetCreateFragment()) // заменяем текущий фрагмент
                    .addToBackStack("BudgetPageFragmentTag")  // добавляем в back stack (чтобы можно было вернуться)
                    .commit();
        });


    }


    void loadExistingBudgetInfo(String month) {
        createBudgetIllustration.setVisibility(View.GONE);
        budgetDescriptionTextView.setVisibility(View.GONE);
        BudgetPieChart.setVisibility(View.VISIBLE);
        budgetActionButton.setText("Смотреть детали");

        budgetActionButton.setOnClickListener(v -> {
            // Переключаемся на BudgetCreateFragment
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new BudgetDetailsPage()) // заменяем текущий фрагмент
                    .addToBackStack(null)  // добавляем в back stack (чтобы можно было вернуться)
                    .commit();
        });
    }




}