package com.example.finsmart.main_activity.budget_page.budget_confirm_page;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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
import com.example.finsmart.main_activity.CurrencyUtils;
import com.example.finsmart.main_activity.budget_page.Budget;
import com.example.finsmart.main_activity.budget_page.BudgetDBHelper;
import com.example.finsmart.main_activity.budget_page.BudgetPageFragment;
import com.example.finsmart.main_activity.budget_page.BudgetUtils;
import com.example.finsmart.main_activity.budget_page.ExpenseEntry;
import com.example.finsmart.main_activity.budget_page.IncomeEntry;
import com.github.mikephil.charting.charts.PieChart;

import java.util.ArrayList;
import java.util.List;

public class BudgetConfirmFragment extends Fragment {
    View view;
    Button createBudgetFinish;
    TextView confirmBudgetIncomesSum, confirmBudgetExpensesSum, confirmBudgetDeltaTitle;
    BudgetDBHelper dbHelper;
    Budget receivedBudget;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_budget_confirm_page, container, false);

        dbHelper = new BudgetDBHelper(requireContext());

        initUI();

        updateBudgetData();

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


            Budget existingBudget = dbHelper.getBudgetByMonth(receivedBudget.getMonth());

            if (existingBudget == null) {
                long newBudgetId = dbHelper.addBudget(receivedBudget);
                Log.d("tmp", "Добавлен бюджет — " + receivedBudget.getMonth());


            } else {
                // Optional: Handle case where budget already exists
                Log.d("tmp", "Budget for " + receivedBudget.getMonth() + " already exists.");
                // You could choose to update it instead or notify the user
            }



            // Удаляем весь стек до фрагмента с тегом "fragment_1_tag"
            requireActivity().getSupportFragmentManager().popBackStack("BudgetPageFragmentTag", FragmentManager.POP_BACK_STACK_INCLUSIVE);

        });
    }

    private void updateBudgetData() {
        if (getArguments() != null) {
            receivedBudget = getArguments().getParcelable("budget");
            if (receivedBudget != null) {
                String incomesSumFormatted = CurrencyUtils.formatDoubleToString
                        (receivedBudget.getTotalIncome(), 0);
                String expensesSumFormatted = CurrencyUtils.formatDoubleToString
                        (receivedBudget.getTotalExpenses(), 0);
                String deltaSumFormatted = CurrencyUtils.formatDoubleToString
                        (receivedBudget.getNetBudget() , 0);

                confirmBudgetIncomesSum.setText(incomesSumFormatted);
                confirmBudgetExpensesSum.setText(expensesSumFormatted);
                confirmBudgetDeltaTitle.setText(deltaSumFormatted);
            }
        }
    }


}