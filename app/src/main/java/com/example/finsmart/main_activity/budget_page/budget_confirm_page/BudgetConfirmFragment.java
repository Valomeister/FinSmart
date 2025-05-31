package com.example.finsmart.main_activity.budget_page.budget_confirm_page;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.finsmart.R;
import com.example.finsmart.main_activity.budget_page.BudgetPageFragment;
import com.github.mikephil.charting.charts.PieChart;

import java.util.ArrayList;
import java.util.List;

public class BudgetConfirmFragment extends Fragment {
    View view;
    Button createBudgetFinish;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_budget_confirm_page, container, false);

        initUI();

        return view;
    }

    private void initUI() {
        createBudgetFinish = view.findViewById(R.id.createBudgetFinish);
        createBudgetFinish.setOnClickListener(v -> {
            // Удаляем весь стек до фрагмента с тегом "fragment_1_tag"
            requireActivity().getSupportFragmentManager().popBackStack("BudgetPageFragmentTag", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            // Переключаемся обратно на BudgetPageFragment
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new BudgetPageFragment()) // заменяем текущий фрагмент
                    .addToBackStack(null)  // добавляем в back stack (чтобы можно было вернуться)
                    .commit();
        });
    }


}