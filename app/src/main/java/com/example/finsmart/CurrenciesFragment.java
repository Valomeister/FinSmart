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

public class CurrenciesFragment extends Fragment {

    private HashMap<String, Integer> currencyIconMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_currencies, container, false);

        setToolbar(inflater, "Валюта");

        LinearLayout currencyContainer = view.findViewById(R.id.currencyContainer);
        ArrayList<Currency> currencies =  getCurrenciesFromDataBase();
        fillCurrencyContainer(currencies, currencyContainer);

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

    ArrayList<Currency> getCurrenciesFromDataBase() {
        ArrayList<Currency> currencies = new ArrayList<>();

        // Добавляем валюты с рандомизированными данными
        // TODO: 29.05.2025 Сделать особое оформление для основной валюты в приложении - рублю,
        // например, не нужно xchange rrate и динамику показывать.
        currencies.add(new Currency("Доллар США", "USD", '$', 150, 74.50, 76.30, "15.11.2024"));
        currencies.add(new Currency("Евро", "EUR", '€', 200, 82.10, 80.50, "10.01.2025"));
        currencies.add(new Currency("Китайский юань", "CNY", '¥', 1000, 10.20, 10.75, "05.02.2025"));
        currencies.add(new Currency("Российский рубль", "RUB", '₽', 15000, 1.0, 1.0, "19.12.2024"));

        return currencies;
    }

    private void fillCurrencyContainer(ArrayList<Currency> currencies, LinearLayout currencyContainer) {
        initCurrencyIcons();
        for (int i = 0; i < currencies.size(); i++) {
            boolean last = i == currencies.size() - 1;
            View currencyItem = createCurrencyItem(currencies.get(i), currencyContainer, last);
            currencyContainer.addView(currencyItem);
        }
    }
    private View createCurrencyItem(Currency currency, LinearLayout  currencyContainer, boolean last) {
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View item = inflater.inflate(R.layout.item_currency, currencyContainer, false);

        // Находим все TextView в currency_item.xml
        TextView tvCurrencyName = item.findViewById(R.id.currencyName);
        TextView tvCurrencyConvertedSum = item.findViewById(R.id.currencyConvertedSum);
        TextView tvCurrencyExchangeRate = item.findViewById(R.id.currencyExchangeRate);
        TextView tvCurrencyQuantity = item.findViewById(R.id.currencyQuantity);
        TextView tvCurrencyPurchaseDate = item.findViewById(R.id.currencyPurchaseDate);
        TextView tvCurrencyPurchaseExchangeRate = item.findViewById(R.id.currencyPurchaseExchangeRate);
        TextView tvCurrencyExchangeRateDynamics = item.findViewById(R.id.currencyExchangeRateDynamics);
        ImageView ivCurrencyIcon = item.findViewById(R.id.currencyIcon);
        ImageView ivEditButton = item.findViewById(R.id.editButton);

        // Устанавливаем данные
        tvCurrencyName.setText(currency.getName());
        String formattedAmount = String.format("%,.0f ₽", currency.getMarketValue())
                .replace(',', ' ');
        tvCurrencyConvertedSum.setText(formattedAmount);
        String formattedCurrentPrice = String.format("%,.0f ₽", currency.getCurrentExchangeRate())
                .replace(',', ' ').replace('.', ',');
        tvCurrencyExchangeRate.setText(formattedCurrentPrice);
        String formattedQuantity = String.format("%,.0f %s", currency.getQuantity(),
                        currency.getSymbol())
                .replace(',', ' ').replace('.', ',');
        tvCurrencyQuantity.setText(formattedQuantity);
        tvCurrencyPurchaseDate.setText(currency.getPurchaseDate());
        String formattedPurchasePrice = String.format("%,.0f ₽", currency.getPurchaseExchangeRate())
                .replace(',', ' ').replace('.', ',');
        tvCurrencyPurchaseExchangeRate.setText(formattedPurchasePrice);
        // TODO: 28.05.2025 сделать убытки красными а прибыль зеленой?
        String formattedDynamics = String.format("%,.2f %%", currency.getReturnPercentage())
                .replace(',', ' ');
        tvCurrencyExchangeRateDynamics.setText(formattedDynamics);

        // Логотип валюты
        ImageView currencyIcon = item.findViewById(R.id.currencyIcon);

        // Получаем ID иконки из словаря по названию банка
        Integer iconResId = currencyIconMap.get(currency.getCode());

        if (iconResId != null) {
            currencyIcon.setImageResource(iconResId);
        } else {
            // Если банк не найден в списке — ставим дефолтную иконку
            currencyIcon.setImageResource(R.drawable.bank_default_icon);
        }


        if (!last) {
            LinearLayout currencyBody = item.findViewById(R.id.currencyBody);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) currencyBody.getLayoutParams();
            int marginBottomInPx = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    11,
                    requireContext().getResources().getDisplayMetrics()
            );
            params.setMargins(0, 0, 0, marginBottomInPx);
            currencyBody.setLayoutParams(params);
        }

        return item;
    }

    // TODO: 29.05.2025 Подобрать нормальные, одинаковые по размеру иконки
    private void initCurrencyIcons() {
        currencyIconMap = new HashMap<>();
        currencyIconMap.put("USD", R.drawable.usd_icon);
        currencyIconMap.put("EUR", R.drawable.eur_icon);
        currencyIconMap.put("CNY", R.drawable.cny_icon);
        currencyIconMap.put("RUB", R.drawable.rub_icon);
    }
}