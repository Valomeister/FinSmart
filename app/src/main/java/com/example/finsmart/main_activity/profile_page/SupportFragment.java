package com.example.finsmart.main_activity.profile_page;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.finsmart.R;

public class SupportFragment extends Fragment {
    View view;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_support, container, false);

        ToolbarUtils.setToolbar(this, "Поддержка");

        initUI();

        return view;
    }

    private void initUI() {

    }
}