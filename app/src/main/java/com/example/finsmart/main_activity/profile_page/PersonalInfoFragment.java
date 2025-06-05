package com.example.finsmart.main_activity.profile_page;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.finsmart.R;

public class PersonalInfoFragment extends Fragment {
    View view;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_personal_info, container, false);

        initUI();

        ToolbarUtils.setToolbar(this, "Личная информация");

        return view;
    }

    private void initUI() {

    }
}