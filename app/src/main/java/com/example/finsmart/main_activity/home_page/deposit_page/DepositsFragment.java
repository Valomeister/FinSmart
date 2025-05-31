package com.example.finsmart.main_activity.home_page.deposit_page;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finsmart.main_activity.MainActivity;
import com.example.finsmart.R;
import com.example.finsmart.main_activity.home_page.HomePageFragment;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DepositsFragment extends Fragment {

    private HashMap<String, Integer> bankIconMap;
    DepositDBHelper dbHelper;
    LinearLayout depositContainer;
    View fragmentView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.fragment_deposits, container, false);

        setToolbar(inflater, "Вклады");

        initBankIcons();

        dbHelper = new DepositDBHelper(requireContext());
        // Для заполнения пустой БД небольшим набором данных:
        dbHelper.populateInitialData();

        depositContainer = fragmentView.findViewById(R.id.depositContainer);
        ArrayList<Deposit> deposits =  getDepositsFromDataBase();
        fillDepositContainer(deposits, depositContainer);

        Button addDepositButton = fragmentView.findViewById(R.id.add_deposit_button);
        addDepositButton.setOnClickListener(v -> showAddDepositBottomSheet());

        return fragmentView;
    }

    public void setToolbar(LayoutInflater inflater, String tabName) {
        MainActivity mainActivity = (MainActivity) requireActivity();
        LinearLayout toolbarLinearLayout = mainActivity.toolbarLinearLayout;
        // скрываем логотип
        ImageView logoImageView = toolbarLinearLayout.findViewById(R.id.logo);
        logoImageView.setVisibility(View.GONE);
        // вставляем свой toolbar
        View toolbarNavBack = inflater.inflate(R.layout.toolbar_nav_back, toolbarLinearLayout, false);
        TextView toolbarNavBackTab = toolbarNavBack.findViewById(R.id.tab);
        toolbarNavBackTab.setText(tabName);

        ImageView arrowBack = toolbarNavBack.findViewById(R.id.arrowBack);
        arrowBack.setOnClickListener(v -> {
            // Возвращаем toolbar к исходному
            toolbarLinearLayout.removeViewAt(1);
            logoImageView.setVisibility(View.VISIBLE);

            // Открытие HomePageFragment
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new HomePageFragment()) // R.id.fragment_container — это ID контейнера в активити
                    .commit();
        });


        toolbarLinearLayout.addView(toolbarNavBack);
    }

    ArrayList<Deposit> getDepositsFromDataBase() {
        ArrayList<Deposit> deposits = (ArrayList<Deposit>) dbHelper.getAllDeposits();

        return deposits;
    }

    private void fillDepositContainer(ArrayList<Deposit> deposits, LinearLayout depositContainer) {
        // Удаляем все существующие элементы
        depositContainer.removeAllViews();

        for (int i = 0; i < deposits.size(); i++) {
            boolean last = i == deposits.size() - 1;
            View depositItem = createDepositItem(deposits.get(i), depositContainer, last);
            depositContainer.addView(depositItem);
        }
    }
    private View createDepositItem(Deposit deposit, LinearLayout depositContainer, boolean last) {
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View item = inflater.inflate(R.layout.item_deposit, depositContainer, false);

        // Находим все TextView
        TextView tvBankName = item.findViewById(R.id.depositBankName);
        TextView tvDepositSum = item.findViewById(R.id.depositSum);
        TextView tvInterest = item.findViewById(R.id.depositInterest);
        TextView tvDate = item.findViewById(R.id.depositDate);
        TextView tvDuration = item.findViewById(R.id.depositDuration);
        TextView tvProlongation = item.findViewById(R.id.depositProlongation);
        TextView tvCapitalization = item.findViewById(R.id.depositCapitalization);
        ImageView ivBankIcon = item.findViewById(R.id.bankIcon);
        ImageView ivEditButton = item.findViewById(R.id.editButton);

        // Устанавливаем данные
        tvBankName.setText(deposit.getBankName());
        String formattedAmount = String.format("%,.0f ₽", deposit.getAmount())
                .replace(',', ' ');
        tvDepositSum.setText(formattedAmount);
        tvInterest.setText(String.format("%.2f%%", deposit.getInterestRate()).
                replace('.', ','));
        tvDate.setText(deposit.getStartDate());
        tvDuration.setText(deposit.getDuration());
        tvProlongation.setText(deposit.isHasProlongation() ? "есть" : "нет");
        tvCapitalization.setText(deposit.isHasCapitalization() ? "есть" : "нет");

        // Обработчик кнопки редактирования
        ivEditButton.setOnClickListener(v -> showEditDepositBottomSheet(deposit));

        // Получаем ID иконки из словаря по названию банка
        Integer iconResId = bankIconMap.get(deposit.getBankName());

        if (iconResId != null) {
            ivBankIcon.setImageResource(iconResId);
        } else {
            // Если банк не найден в списке — ставим дефолтную иконку
            ivBankIcon.setImageResource(R.drawable.bank_default_icon);
        }


        if (!last) {
            LinearLayout depositBody = item.findViewById(R.id.depositBody);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) depositBody.getLayoutParams();
            int marginBottomInPx = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    11,
                    requireContext().getResources().getDisplayMetrics()
            );
            params.setMargins(0, 0, 0, marginBottomInPx);
            depositBody.setLayoutParams(params);
        }


        return item;
    }

    private void showAddDepositBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_deposit, null);
        bottomSheetDialog.setContentView(dialogView);

        EditText editBankName = dialogView.findViewById(R.id.editBankName);
        EditText editDepositedSum = dialogView.findViewById(R.id.editDepositedSum);
        EditText editDepositRate = dialogView.findViewById(R.id.editDepositRate);
        EditText editDepositDate = dialogView.findViewById(R.id.editDepositDate);
        EditText editDepositTerm = dialogView.findViewById(R.id.editDepositTerm);
        Button buttonAdd = dialogView.findViewById(R.id.buttonAdd);

        // Пример использования Switch для пролонгации и капитализации (если добавишь их позже)
        // Switch switchProlongation = dialogView.findViewById(R.id.switchProlongation);
        // Switch switchCapitalization = dialogView.findViewById(R.id.switchCapitalization);

        buttonAdd.setOnClickListener(v -> {
            String bankName = editBankName.getText().toString().trim();
            String depositDate = editDepositDate.getText().toString().trim();

            // Валидация обязательных полей
            if (bankName.isEmpty() || depositDate.isEmpty()) {
                Toast.makeText(requireContext(), "Заполните все обязательные поля", Toast.LENGTH_SHORT).show();
                return;
            }

            double depositedSum = 0;
            double depositRate = 0;
            int depositTerm = 0;

            try {
                depositedSum = Double.parseDouble(editDepositedSum.getText().toString());
                depositRate = Double.parseDouble(editDepositRate.getText().toString());
                depositTerm = Integer.parseInt(editDepositTerm.getText().toString());
            } catch (NumberFormatException e) {
                Toast.makeText(requireContext(), "Некорректный формат числа", Toast.LENGTH_SHORT).show();
                return;
            }

            // Проверка положительных значений
            if (depositedSum <= 0 || depositRate <= 0 || depositTerm <= 0) {
                Toast.makeText(requireContext(), "Сумма, ставка и срок должны быть больше нуля", Toast.LENGTH_SHORT).show();
                return;
            }

            // Предположим, что длительность хранится как "X месяцев"
            String duration = depositTerm + " месяцев";

            // Можно добавить выбор: капитализация и пролонгация — например через Switch
            boolean hasProlongation = false; // Заглушка, можно изменить при наличии UI-элементов
            boolean hasCapitalization = false;

            // Проверяем, существует ли уже депозит с таким банком
            if (dbHelper.depositExists(bankName)) {
                Toast.makeText(requireContext(), "Депозит в этом банке уже добавлен", Toast.LENGTH_SHORT).show();
                return;
            }

            // Добавляем депозит в БД
            dbHelper.addDeposit(new Deposit(
                    bankName,
                    depositedSum,
                    depositRate,
                    depositDate,
                    duration,
                    hasProlongation,
                    hasCapitalization
            ));

            // Обновляем UI (заменить на реальную логику отображения)
            List<Deposit> updatedList = dbHelper.getAllDeposits();
            fillDepositContainer((ArrayList<Deposit>) updatedList, depositContainer);

            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();
    }

    private void showEditDepositBottomSheet(Deposit deposit) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_deposit, null);
        bottomSheetDialog.setContentView(dialogView);

        TextView editBankName = dialogView.findViewById(R.id.editBankName);
        EditText editDepositedSum = dialogView.findViewById(R.id.editDepositedSum);
        EditText editDepositRate = dialogView.findViewById(R.id.editDepositRate);
        EditText editDepositDate = dialogView.findViewById(R.id.editDepositDate);
        EditText editDepositTerm = dialogView.findViewById(R.id.editDepositTerm);
        Button buttonSave = dialogView.findViewById(R.id.buttonSave); // В нашем случае "Добавить" — это "Сохранить"
        Button buttonDelete = dialogView.findViewById(R.id.buttonDelete); // Предположим, что кнопка удаления есть

        // Заполняем поля текущими данными
        editBankName.setText(deposit.getBankName());
        editDepositedSum.setText(String.valueOf(deposit.getAmount()));
        editDepositRate.setText(String.valueOf(deposit.getInterestRate()));
        editDepositDate.setText(deposit.getStartDate());

        // Извлекаем срок из строки вроде "6 месяцев" -> только число
        String durationText = deposit.getDuration();
        int termMonths = 6; // значение по умолчанию
        if (durationText != null && !durationText.isEmpty()) {
            try {
                termMonths = Integer.parseInt(durationText.split(" ")[0]);
            } catch (Exception ignored) {}
        }
        editDepositTerm.setText(String.valueOf(termMonths));

        // Показываем диалог
        bottomSheetDialog.show();

        // --- Логика сохранения ---
        buttonSave.setOnClickListener(v -> {
            String bankName = editBankName.getText().toString().trim();
            String depositDate = editDepositDate.getText().toString().trim();
            String depositedSumStr = editDepositedSum.getText().toString();
            String depositRateStr = editDepositRate.getText().toString();
            String depositTermStr = editDepositTerm.getText().toString();

            // Проверяем заполненность полей
            if (bankName.isEmpty() || depositDate.isEmpty() ||
                    depositedSumStr.isEmpty() || depositRateStr.isEmpty() || depositTermStr.isEmpty()) {
                Toast.makeText(requireContext(), "Заполните все поля", Toast.LENGTH_SHORT).show();
                return;
            }

            double depositedSum = 0;
            double depositRate = 0;
            int depositTerm = 0;

            try {
                depositedSum = Double.parseDouble(depositedSumStr);
                depositRate = Double.parseDouble(depositRateStr);
                depositTerm = Integer.parseInt(depositTermStr);
            } catch (NumberFormatException e) {
                Toast.makeText(requireContext(), "Неверный формат числа", Toast.LENGTH_SHORT).show();
                return;
            }

            if (depositedSum <= 0 || depositRate <= 0 || depositTerm <= 0) {
                Toast.makeText(requireContext(), "Сумма, ставка и срок должны быть больше нуля", Toast.LENGTH_SHORT).show();
                return;
            }

            String duration = depositTerm + " месяцев";

            // Создаём обновлённый объект Deposit
            Deposit updatedDeposit = new Deposit(
                    bankName,
                    depositedSum,
                    depositRate,
                    depositDate,
                    duration,
                    deposit.isHasProlongation(), // пока не редактируется, можно оставить как есть
                    deposit.isHasCapitalization()
            );

            // Обновляем запись в БД
            dbHelper.updateDeposit(updatedDeposit);

            // Обновляем UI
            List<Deposit> updatedList = dbHelper.getAllDeposits();
            fillDepositContainer((ArrayList<Deposit>) updatedList, depositContainer); // заменить на реальный метод отрисовки

            bottomSheetDialog.dismiss();
        });

        // --- Логика удаления ---
        if (buttonDelete != null) {
            buttonDelete.setOnClickListener(v -> {
                // Удаляем депозит по имени банка
                dbHelper.deleteDeposit(deposit);

                // Обновляем UI
                List<Deposit> updatedList = dbHelper.getAllDeposits();
                fillDepositContainer((ArrayList<Deposit>) updatedList, depositContainer); // заменить на реальный метод отрисовки

                bottomSheetDialog.dismiss();

                // Показываем сообщение
                Toast.makeText(requireContext(), "Вклад удалён", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void initBankIcons() {
        bankIconMap = new HashMap<>();
        bankIconMap.put("Сбербанк", R.drawable.sber_icon);
        bankIconMap.put("Т-Банк", R.drawable.tbank_icon);
        bankIconMap.put("ВТБ", R.drawable.vtb_icon);
        bankIconMap.put("Альфа-Банк", R.drawable.alfabank_icon);
        bankIconMap.put("Газпромбанк", R.drawable.gazprombank_icon);
        bankIconMap.put("Райффайзен Банк", R.drawable.raiffeisenbank_icon);
    }
}