package com.example.finsmart.main_activity.budget_page;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.finsmart.R;
import com.example.finsmart.data.database.AppDatabase;
import com.example.finsmart.data.model.Category;
import com.example.finsmart.data.repository.AppRepository;
import com.example.finsmart.main_activity.CurrencyUtils;
import com.example.finsmart.main_activity.budget_page.budget_create_page.BudgetCreateFragment;
import com.example.finsmart.main_activity.budget_page.budget_details_page.BudgetDetailsPage;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;


public class BudgetPageFragment extends Fragment {

    View view;
    ImageView createBudgetIllustration;
    PieChart BudgetPieChart;
    Button budgetActionButton;
    TextView budgetDescriptionTextView;

    AppRepository appRepository;
    AppDatabase appDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_budget_page, container, false);

        appDatabase = AppDatabase.getInstance(requireContext());

        appRepository = new AppRepository(
                appDatabase.userDao(),
                appDatabase.budgetDao(),
                appDatabase.categoryDao(),
                appDatabase.operationDao(),
                appDatabase
        );


        createBudgetIllustration = view.findViewById(R.id.createBudgetIllustration);
        budgetDescriptionTextView = view.findViewById(R.id.budgetDescription);
        BudgetPieChart = view.findViewById(R.id.BudgetPieChart);
        budgetActionButton = view.findViewById(R.id.budgetActionButton);



        loadBudgetSection();
//        loadCreateBudgetSection();

        return view;
    }

    void loadBudgetSection() {
        String currentMonth = BudgetUtils.getCurrentMonth();
        // Получаем ViewModel
        appRepository.getBudgetByMonth(currentMonth).observe(getViewLifecycleOwner(), budget -> {
            if (budget == null) {
                // Бюджета на текущий месяц нет
                loadCreateBudgetSection();
            } else {
                // Бюджет на текущий месяц есть
                loadExistingBudgetInfo(currentMonth);
            }
        });
    }

    void loadCreateBudgetSection() {
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

//        budgetActionButton.setOnClickListener(v -> {
//            // Переключаемся на BudgetCreateFragment
//            getParentFragmentManager().beginTransaction()
//                    .replace(R.id.fragment_container, new BudgetDetailsPage()) // заменяем текущий фрагмент
//                    .addToBackStack(null)  // добавляем в back stack (чтобы можно было вернуться)
//                    .commit();
//        });
//
//        appRepository.getBudgetByMonth(month).observe(getViewLifecycleOwner(), budget -> {
//            int budgetId = budget.getBudgetId();
//
//        });
//        setupPieChart(CurrentBudget);
    }

    private void setupPieChart(Category categories) {
//        List<PieEntry> entries = new ArrayList<>();
//
//        // Доходы
//        for (ExpenseEntry income : budget.getExpenseList()) {
//            if (income.getAmount() > 0) {
//                entries.add(new PieEntry((float) income.getAmount(), income.getName()));
//            }
//        }
//
//        // Настройка набора данных
//        PieDataSet dataSet = new PieDataSet(entries, "Расходы");
//        dataSet.setDrawValues(false);
//        dataSet.setColors(CategoryProvider.getDefaultColors());
//        dataSet.setValueTextColor(Color.BLACK);
//        dataSet.setValueTextSize(14f);
//
//
//
//        // Нагоняем стиля
//        BudgetPieChart.setDrawEntryLabels(false);      // Надписи у секторов
//        BudgetPieChart.getLegend().setEnabled(false);  // Легенда
//        BudgetPieChart.getDescription().setEnabled(false); // Описание
//        BudgetPieChart.setTransparentCircleAlpha(0); // Устанавливаем полную прозрачность
//        BudgetPieChart.setHoleRadius(76f); // 76% радиуса (размер отверстия в центре)
//        String budgetExpensesFormatted = CurrencyUtils.formatDoubleToString
//                (budget.getTotalExpenses(), 0);
//        BudgetPieChart.setCenterText(budgetExpensesFormatted);
//        BudgetPieChart.setCenterTextSize(22f); // Размер в dp
//        BudgetPieChart.setCenterTextTypeface(Typeface.DEFAULT_BOLD);
//        BudgetPieChart.setCenterTextColor(Color.parseColor("#4D4D4D"));
//
//
//        // Настройка самой диаграммы
//        PieData pieData = new PieData(dataSet);
//        BudgetPieChart.setData(pieData);
//        BudgetPieChart.invalidate();
    }


}