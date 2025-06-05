package com.example.finsmart.main_activity.profile_page;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.finsmart.R;
import com.example.finsmart.main_activity.budget_page.budget_details_page.BudgetDetailsPage;

public class ProfilePageFragment extends Fragment {
    View view;
    LinearLayout personalInfoLayout, dataManagementLayout, settingsLayout,
            supportLayout, logoutLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile_page, container, false);

        initUI();

        return view;
    }

    private void initUI() {
        personalInfoLayout = view.findViewById(R.id.personalInfoLayout);
        dataManagementLayout = view.findViewById(R.id.dataManagementLayout);
        settingsLayout = view.findViewById(R.id.settingsLayout);
        supportLayout = view.findViewById(R.id.supportLayout);
        logoutLayout = view.findViewById(R.id.logoutLayout);

        personalInfoLayout.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new PersonalInfoFragment()) // заменяем текущий фрагмент
                    .addToBackStack(null)  // добавляем в back stack (чтобы можно было вернуться)
                    .commit();
        });

        dataManagementLayout.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new DataManagementFragment()) // заменяем текущий фрагмент
                    .addToBackStack(null)  // добавляем в back stack (чтобы можно было вернуться)
                    .commit();
        });

        settingsLayout.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new SettingsFragment()) // заменяем текущий фрагмент
                    .addToBackStack(null)  // добавляем в back stack (чтобы можно было вернуться)
                    .commit();
        });

        supportLayout.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new SupportFragment()) // заменяем текущий фрагмент
                    .addToBackStack(null)  // добавляем в back stack (чтобы можно было вернуться)
                    .commit();
        });
    }
}