package com.example.finsmart;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.util.regex.Pattern;

public class LoginFragment extends Fragment {

    DatabaseHelper dbHelper;

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

        // --- Добавляем обработчик для кнопки входа ---
        loginButton.setOnClickListener(v -> {
            // Считываем данные из полей
            String phone = loginEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            // Хэшируем пароль (если нужно)
            String passwordHash = HashUtils.sha256(password);

            // Логируем введённые данные
            Log.d("my_test", "Телефон: " + phone);
            Log.d("my_test", "Пароль (hash): " + passwordHash);


            dbHelper = new DatabaseHelper(requireContext());

            TextView loginErrorText = view.findViewById(R.id.loginError);
            TextView passwordErrorText = view.findViewById(R.id.passwordError);

            boolean phoneOK = false;
            boolean passwordOK = false;

            String regexPhone = "^(\\+7|8)\\d{10}$";
            // проверка поля номера телефона на пустоту
            if (phone.isEmpty()) {
                loginErrorText.setVisibility(View.VISIBLE);
                loginErrorText.setText("Обязательное поле!");
            }
            // Проверяем что телефон не занят
            else if (!dbHelper.isPhoneExists(phone)) {
                loginErrorText.setVisibility(View.VISIBLE);
                loginErrorText.setText("Номер не зарегистрирован!");
            }
            else {
                loginErrorText.setVisibility(View.INVISIBLE);
                phoneOK = true;
            }


            // проверка поля пароля на пустоту
            if (password.isEmpty()) {
                passwordErrorText.setVisibility(View.VISIBLE);
                passwordErrorText.setText("Обязательное поле!");
            }
            // Проверяем что пароль верен
            else if (!dbHelper.checkUser(phone, password)) {
                passwordErrorText.setVisibility(View.VISIBLE);
                passwordErrorText.setText("Неверный пароль!");
            }
            else {
                passwordErrorText.setVisibility(View.INVISIBLE);
                passwordOK = true;
            }



            if (phoneOK && passwordOK) {
                Toast.makeText(getActivity(), "Вы вошли в учетную запись", Toast.LENGTH_SHORT).show();


                Intent intent = new Intent(requireActivity(), MainActivity.class);
                startActivity(intent);
            }
        });

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