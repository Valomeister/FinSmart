package com.example.finsmart;

import java.util.regex.Pattern;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

public class ResetPasswordFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createNotificationChannel();
    }

    UserDatabaseHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reset_password, container, false);


        // Находим элементы
        EditText loginEditText = view.findViewById(R.id.loginEditText);
        EditText smsCodeEditText = view.findViewById(R.id.smsCodeEditText);
        Button requestCodeButton = view.findViewById(R.id.requestCodeButton);
        EditText passwordEditText = view.findViewById(R.id.passwordEditText);
        EditText confirmPasswordEditText = view.findViewById(R.id.confirmPasswordEditText);
        Button savePasswordButton = view.findViewById(R.id.savePasswordButton);
        TextView registerTextView = view.findViewById(R.id.registerLinkTextView);

        requestCodeButton.setOnClickListener(v -> {
            String phone = loginEditText.getText().toString().trim();
            TextView loginErrorText = view.findViewById(R.id.phoneError);
            // проверка поля номера телефона на пустоту
            if (phone.isEmpty()) {
                loginErrorText.setVisibility(View.VISIBLE);
                loginErrorText.setText("Обязательное поле!");
            }
            else {
                checkNotificationPermission();
                loginErrorText.setVisibility(View.INVISIBLE);
            }
        });

        // --- Добавляем обработчик для кнопки регистрации ---
        savePasswordButton.setOnClickListener(v -> {
            // Считываем данные из полей
            String phone = loginEditText.getText().toString().trim();
            String smsCode = smsCodeEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();

            // Хэшируем пароль
            String passwordHash = HashUtils.sha256(password);

            dbHelper = new UserDatabaseHelper(requireContext());

            TextView loginErrorText = view.findViewById(R.id.phoneError);
            TextView smsCodeErrorText = view.findViewById(R.id.smsCodeError);
            TextView passwordErrorText = view.findViewById(R.id.passwordError);
            TextView confirmPasswordErrorText = view.findViewById(R.id.confirmPasswordError);

            boolean phoneOK = false;
            boolean passwordOK = false;
            boolean confirmPasswordOK = false;
            boolean smsCodeOK = false;

            // проверка поля номера телефона на пустоту
            if (phone.isEmpty()) {
                loginErrorText.setVisibility(View.VISIBLE);
                loginErrorText.setText("Обязательное поле!");
            }
            // Проверяем что телефон зарегистрирован
            else if (!dbHelper.isPhoneExists(phone)) {
                loginErrorText.setVisibility(View.VISIBLE);
                loginErrorText.setText("Номер не зарегистрирован!");
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


            if (smsCode.isEmpty()) {
                smsCodeErrorText.setVisibility(View.VISIBLE);
                smsCodeErrorText.setText("Обязательное поле!");
            }
            else if (!smsCode.equals("512713")) {
                smsCodeErrorText.setVisibility(View.VISIBLE);
                smsCodeErrorText.setText("Неверный код!");
            }
            else {
                smsCodeErrorText.setVisibility(View.INVISIBLE);
                smsCodeOK = true;
            }

            if (phoneOK && smsCodeOK && passwordOK && confirmPasswordOK) {
                dbHelper.updateUserPassword(phone, password);
                Toast.makeText(getActivity(), "Вы успешно поменяли пароль", Toast.LENGTH_SHORT).show();
                switchToLoginFragment();


                // Пример вывода Toast можно добавить позже

                // Переход на главную Activity или проверка
                // Intent intent = new Intent(requireContext(), MainActivity.class);
                // startActivity(intent);
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

    public void switchToLoginFragment() {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new LoginFragment())
                .commit();
    }


    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (requireContext().checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            } else {
                sendNotification(); // Уже есть разрешение
            }
        } else {
            sendNotification(); // Для более старых версий не требуется разрешение
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "My Channel";
            String description = "Description of the channel";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("channel_id", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = requireContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void sendNotification() {
        createNotificationChannel();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), "channel_id")
                .setSmallIcon(R.drawable.baseline_message_24)
                .setContentTitle("Код восстановления")
                .setContentText("Ваш код восстановления пароля учетной записи: 512713. Никому его не сообщайте!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true); // Закрыть уведомление по нажатию

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(requireContext());
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        notificationManager.notify(1, builder.build()); // ID уведомления — 1
    }
}