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

public class FundsFragment extends Fragment {

    private HashMap<String, Integer> fundIconMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_funds, container, false);

        setToolbar(inflater, "Фонды");

        LinearLayout fundContainer = view.findViewById(R.id.fundContainer);
        ArrayList<Fund> funds =  getFundsFromDataBase();
        fillFundContainer(funds, fundContainer);

        return view;
    }

    // TODO: 28.05.2025 в DepositsFragment и FundsFragment дублируется тело этого метода
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

    ArrayList<Fund> getFundsFromDataBase() {
        ArrayList<Fund> funds = new ArrayList<>();

        // Добавляем депозиты вручную
        funds.add(new Fund("АО ВИМ Инвестиции", 290000, 17.0, "19.12.2024"));
        funds.add(new Fund("Альфа-Капитал", 520000, 18.7, "05.01.2025"));
        funds.add(new Fund("УК «Первая»", 145000, 16.2, "12.11.2024"));
        funds.add(new Fund("УК «Брокеркредитсервис»", 280000, 17.3, "22.12.2024"));
        funds.add(new Fund("Атон-менеджмент", 300000, 16.8, "17.12.2024"));
        funds.add(new Fund("Финам Менеджмент", 275000, 17.1, "20.12.2024"));
        funds.add(new Fund("УК «ААА Управление Капиталом»", 310000, 16.9, "18.12.2024"));
        funds.add(new Fund("Т-Капитал", 295000, 17.2, "21.12.2024"));

        return funds;
    }

    private void fillFundContainer(ArrayList<Fund> funds, LinearLayout fundContainer) {
        initFundIcons();
        for (int i = 0; i < funds.size(); i++) {
            boolean last = i == funds.size() - 1;
            View fundItem = createFundItem(funds.get(i), fundContainer, last);
            fundContainer.addView(fundItem);
        }
    }
    private View createFundItem(Fund fund, LinearLayout fundContainer, boolean last) {
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View item = inflater.inflate(R.layout.item_fund, fundContainer, false);

        // Находим все TextView
        TextView tvFundName = item.findViewById(R.id.fundName);
        TextView tvFundSum = item.findViewById(R.id.fundSum);
        TextView tvFundDynamics = item.findViewById(R.id.fundDynamics);
        TextView tvFundDate = item.findViewById(R.id.fundDate);

        // Устанавливаем данные
        tvFundName.setText(fund.getFundName());
        String formattedAmount = String.format("%,.0f ₽", fund.getAmount())
                .replace(',', ' ');
        tvFundSum.setText(formattedAmount);
        tvFundDynamics.setText(String.format("%.2f%%", fund.getDynamics()).
                replace('.', ','));
        tvFundDate.setText(fund.getStartDate());

        // Логотип банка
        ImageView fundIcon = item.findViewById(R.id.fundIcon);

        // Получаем ID иконки из словаря по названию банка
        Integer iconResId = fundIconMap.get(fund.getFundName());

        if (iconResId != null) {
            fundIcon.setImageResource(iconResId);
        } else {
            // Если банк не найден в списке — ставим дефолтную иконку
            fundIcon.setImageResource(R.drawable.bank_default_icon);
        }


        if (!last) {
            LinearLayout fundBody = item.findViewById(R.id.fundBody);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) fundBody.getLayoutParams();
            int marginBottomInPx = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    11,
                    requireContext().getResources().getDisplayMetrics()
            );
            params.setMargins(0, 0, 0, marginBottomInPx);
            fundBody.setLayoutParams(params);
        }

        return item;
    }

    private void initFundIcons() {
        fundIconMap = new HashMap<>();
        fundIconMap.put("АО ВИМ Инвестиции", R.drawable.ao_vim_investments_icon);
        fundIconMap.put("Альфа-Капитал", R.drawable.alpha_capital_icon);
        fundIconMap.put("УК «Первая»", R.drawable.pervaya_icon);
        fundIconMap.put("УК «Брокеркредитсервис»", R.drawable.brokercreditservice_icon);
        fundIconMap.put("Атон-менеджмент", R.drawable.aton_management_icon);
        fundIconMap.put("Финам Менеджмент", R.drawable.finam_management);
        fundIconMap.put("УК «ААА Управление Капиталом»", R.drawable.aaa_capital_management);
        fundIconMap.put("Т-Капитал", R.drawable.t_capital_icon);
    }
}