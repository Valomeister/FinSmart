package com.example.finsmart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class LoginFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // Находим элементы
        EditText loginEditText = view.findViewById(R.id.loginEditText);
        EditText passwordEditText = view.findViewById(R.id.passwordEditText);
        CheckBox rememberMeCheckbox = view.findViewById(R.id.rememberMeCheckbox);
        Button loginButton = view.findViewById(R.id.loginButton);
        TextView forgotPasswordTextView = view.findViewById(R.id.forgotPasswordTextView);
        TextView registerTextView = view.findViewById(R.id.registerLinkTextView);

        // Обработка нажатия на "Зарегистрироваться"
        registerTextView.setOnClickListener(v -> {
            // Переключаем на RegisterFragment
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new RegisterFragment())
                    .commit();
        });

        return view;
    }
}