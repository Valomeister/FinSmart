package com.example.finsmart;

import java.util.regex.Pattern;

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

public class RegisterFragment extends Fragment {

    DatabaseHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        // Находим элементы
        EditText loginEditText = view.findViewById(R.id.loginEditText);
        EditText passwordEditText = view.findViewById(R.id.passwordEditText);
        EditText confirmPasswordEditText = view.findViewById(R.id.confirmPasswordEditText);
        CheckBox termsCheckbox = view.findViewById(R.id.termsCheckbox);
        Button registerButton = view.findViewById(R.id.registerButton);
        TextView loginTextView = view.findViewById(R.id.loginTextView);

        // Костыль, не знаю как это надо делать по-нормальному...
        termsCheckbox.setOnClickListener(v -> {
            if (!termsCheckbox.isChecked()) {
                termsCheckbox.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#4D4D4D")));
            } else {
                termsCheckbox.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#7D7AFF")));
            }
        });

        // --- Добавляем обработчик для кнопки регистрации ---
        registerButton.setOnClickListener(v -> {
            // Считываем данные из полей
            String phone = loginEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();

            // Хэшируем пароль (если нужно)
            String passwordHash = HashUtils.sha256(password);

            dbHelper = new DatabaseHelper(requireContext());

            TextView loginErrorText = view.findViewById(R.id.loginError);
            TextView passwordErrorText = view.findViewById(R.id.passwordError);
            TextView confirmPasswordErrorText = view.findViewById(R.id.confirmPasswordError);

            boolean phoneOK = false;
            boolean passwordOK = false;
            boolean confirmPasswordOK = false;
            boolean termsCheckboxOK = false;

            String regexPhone = "^(\\+7|8)\\d{10}$";
            // проверка поля номера телефона на пустоту
            if (phone.isEmpty()) {
                loginErrorText.setVisibility(View.VISIBLE);
                loginErrorText.setText("Обязательное поле!");
            }
            // Проверяем что формат соответствует российкому телефону
            else if (!Pattern.matches(regexPhone, phone)) {
                loginErrorText.setVisibility(View.VISIBLE);
                loginErrorText.setText("Неверный формат номера!");
            }
            // Проверяем что телефон не занят
            else if (dbHelper.isPhoneExists(phone)) {
                loginErrorText.setVisibility(View.VISIBLE);
                loginErrorText.setText("Номер занят!");
            }
            else {
                loginErrorText.setVisibility(View.INVISIBLE);
                phoneOK = true;
            }


            String regexPassword = "^[a-zA-Z0-9!#$&_]+$";
            // проверка поля пароля на пустоту
            if (password.isEmpty()) {
                passwordErrorText.setVisibility(View.VISIBLE);
                passwordErrorText.setText("Обязательное поле!");
            }
            // Проверяем что пароль содержит только додпустимые символы
            else if (!Pattern.matches(regexPassword, password)) {
                passwordErrorText.setVisibility(View.VISIBLE);
                passwordErrorText.setText("Недопустимые символы!");
            }
            // Проверяем что длина пароля корректна
            else if (password.length() < 8 || password.length() > 16) {
                passwordErrorText.setVisibility(View.VISIBLE);
                passwordErrorText.setText("Длина пароля от 8 до 16 символов!");
            }
            else {
                passwordErrorText.setVisibility(View.INVISIBLE);
                passwordOK = true;
            }

            // проверка поля подтверддения пароля на пустоту
            if (confirmPassword.isEmpty()) {
                confirmPasswordErrorText.setVisibility(View.VISIBLE);
                confirmPasswordErrorText.setText("Обязательное поле!");
            }
            // Проверяем что повторение пароля соответствует паролю
            else if (!password.equals(confirmPassword)) {
                confirmPasswordErrorText.setVisibility(View.VISIBLE);
                confirmPasswordErrorText.setText("Пароли не совпадают!");
            }
            else {
                confirmPasswordErrorText.setVisibility(View.INVISIBLE);
                confirmPasswordOK = true;
            }


            if (!termsCheckbox.isChecked()) {
                termsCheckbox.setButtonTintList(ColorStateList.valueOf(Color.RED));
            }
            else {
                termsCheckboxOK = true;
            }

            if (phoneOK && passwordOK && confirmPasswordOK && termsCheckboxOK) {
                dbHelper.registerUser(phone, password);
                Toast.makeText(getActivity(), "Вы создали учетную запись", Toast.LENGTH_SHORT).show();
                switchToLoginFragment();


                // Пример вывода Toast можно добавить позже

                // Переход на главную Activity или проверка
                // Intent intent = new Intent(requireContext(), MainActivity.class);
                // startActivity(intent);
            }

        });

        // Обработка нажатия на "Войти"
        loginTextView.setOnClickListener(v -> {
            // Переключаем на LoginFragment
            switchToLoginFragment();
        });

        return view;
    }

    public void switchToLoginFragment() {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new LoginFragment())
                .commit();
    }
}