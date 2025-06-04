package com.example.finsmart.main_activity.operations_page;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.finsmart.MyApplication;
import com.example.finsmart.R;
import com.example.finsmart.data.model.Operation;
import com.example.finsmart.main_activity.budget_page.BudgetUtils;
import com.example.finsmart.main_activity.budget_page.SharedBudgetViewModel;
import com.example.finsmart.main_activity.budget_page.SharedBudgetViewModelFactory;
import com.example.finsmart.main_activity.operations_page.DayGroupAdapter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class OperationsPageFragment extends Fragment {

    View view;
    SharedBudgetViewModel viewModel;
    RecyclerView recyclerView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_operations_page, container, false);

        MyApplication app = (MyApplication) requireActivity().getApplication();

        SharedBudgetViewModelFactory factory = new SharedBudgetViewModelFactory(
                requireActivity().getApplication(),
                app.repository
        );

        viewModel = new ViewModelProvider(this, factory).get(SharedBudgetViewModel.class);

        viewModel.printDatabaseContent();

        initUI();

        displayOperations();

        return view;
    }

    private void initUI() {
        recyclerView = view.findViewById(R.id.recyclerView);
    }


    private void displayOperations() {
        viewModel.getBudgetByMonth(BudgetUtils.getCurrentMonth()).observe(getViewLifecycleOwner(), budget -> {
            int budgetId = budget.getBudgetId();

            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            DayGroupAdapter adapter = new DayGroupAdapter();
            recyclerView.setAdapter(adapter);

            SharedBudgetViewModel viewModel = new ViewModelProvider(this)
                    .get(SharedBudgetViewModel.class);

            viewModel.getOperationsByBudget(budgetId).observe(getViewLifecycleOwner(), operations -> {
                List<OperationWithDate> grouped = groupOperationsByDate(operations);
                adapter.submitList(grouped);
            });

        });

    }

    private List<OperationWithDate> groupOperationsByDate(List<Operation> operations) {
        List<Operation> sortedOperations = new ArrayList<>(operations);
        sortedOperations.sort((op1, op2) -> {
            return op2.getDate().compareTo(op1.getDate()); // новое - вверху
        });

        Map<String, List<Operation>> map = new LinkedHashMap<>();

        for (Operation op : sortedOperations) {
            map.putIfAbsent(op.getDate(), new ArrayList<>());
            map.get(op.getDate()).add(op);
        }

        List<OperationWithDate> result = new ArrayList<>();

        for (Map.Entry<String, List<Operation>> entry : map.entrySet()) {
            result.add(new OperationWithDate(entry.getKey(), null)); // Добавляем заголовок дня

            for (Operation op : entry.getValue()) {
                result.add(new OperationWithDate(entry.getKey(), op)); // Каждая операция
            }
        }

        result.get(0).setFirst(true);

        return result;
    }

    private List<Operation> sortOperationsByDate(List<Operation> operations) {
        // Сортируем по дате (от новых к старым)
        operations.sort((op1, op2) -> {
            return op2.getDate().compareTo(op1.getDate());
        });
        return operations;
    }





}