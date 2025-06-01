package com.example.finsmart.main_activity.budget_page.budget_details_page;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.finsmart.R;
import com.example.finsmart.main_activity.budget_page.BudgetDBHelper;

public class BudgetDetailsPage extends Fragment {

    View view;
    BudgetDBHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_budget_details_page, container, false);

        dbHelper = new BudgetDBHelper(requireContext());

        return view;
    }


}