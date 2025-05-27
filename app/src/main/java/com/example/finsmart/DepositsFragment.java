package com.example.finsmart;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DepositsFragment extends Fragment {

    private HashMap<String, Integer> bankIconMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_deposits, container, false);

        setToolbar(inflater, "Вклады");

        LinearLayout depositContainer = view.findViewById(R.id.depositContainer);
        ArrayList<Deposit> deposits =  getDepositsFromDataBase();
        fillDepositContainer(deposits, depositContainer);

        return view;
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
        ArrayList<Deposit> deposits = new ArrayList<>();

        // Добавляем депозиты вручную
        deposits.add(new Deposit("Сбербанк", 290000, 17.0, "19.12.2024", "6 месяцев", true, true));
        deposits.add(new Deposit("Т-Банк", 500000, 18.5, "01.01.2025", "12 месяцев", false, true));
        deposits.add(new Deposit("ВТБ", 150000, 16.0, "15.11.2024", "3 месяца", true, false));
        deposits.add(new Deposit("Альфа-Банк", 290000, 17.0, "19.12.2024", "6 месяцев", true, true));
        deposits.add(new Deposit("Газпромбанк", 290000, 17.0, "19.12.2024", "6 месяцев", true, true));
        deposits.add(new Deposit("Райффайзен Банк", 290000, 17.0, "19.12.2024", "6 месяцев", true, true));
        deposits.add(new Deposit("Райффайзен Бунк", 290000, 17.0, "19.12.2024", "6 месяцев", true, true));

        return deposits;
    }

    private void fillDepositContainer(ArrayList<Deposit> deposits, LinearLayout depositContainer) {
        initBankIcons();
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

        // Логотип банка
        ImageView bankIcon = item.findViewById(R.id.bankIcon);

        // Получаем ID иконки из словаря по названию банка
        Integer iconResId = bankIconMap.get(deposit.getBankName());

        if (iconResId != null) {
            bankIcon.setImageResource(iconResId);
        } else {
            // Если банк не найден в списке — ставим дефолтную иконку
            bankIcon.setImageResource(R.drawable.bank_default_icon);
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