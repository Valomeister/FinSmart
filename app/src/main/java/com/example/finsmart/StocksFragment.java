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

public class StocksFragment extends Fragment {

    private HashMap<String, Integer> stockIconMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_stocks, container, false);

        setToolbar(inflater, "Акции");

        LinearLayout stockContainer = view.findViewById(R.id.stockContainer);
        ArrayList<Stock> stocks =  getStocksFromDataBase();
        fillStockContainer(stocks, stockContainer);

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

    ArrayList<Stock> getStocksFromDataBase() {
        ArrayList<Stock> stocks = new ArrayList<>();

        // Добавляем акции вручную с рандомизированными данными
        stocks.add(new Stock("Банк ВТБ", 1512, 75.20,
                93.59, "19.12.2024"));       // Прибыль
        stocks.add(new Stock("ФосАгро", 1242, 80.45,
                72.30, "15.11.2024"));       // Убыток
        stocks.add(new Stock("МТС", 1421, 68.75,
                81.00, "10.01.2025"));           // Прибыль
        stocks.add(new Stock("ЛУКОЙЛ", 1012, 100.00,
                90.00, "05.02.2025"));       // Убыток
        stocks.add(new Stock("Сбер Банк", 1375, 77.32,
                93.59, "19.12.2024"));     // Прибыль
        stocks.add(new Stock("Яндекс", 1198, 85.00,
                78.40, "01.03.2025"));        // Убыток
        stocks.add(new Stock("Московская биржа", 1660, 70.00,
                88.25, "25.12.2024")); // Прибыль

        return stocks;
    }

    private void fillStockContainer(ArrayList<Stock> stocks, LinearLayout stockContainer) {
        initStockIcons();
        for (int i = 0; i < stocks.size(); i++) {
            boolean last = i == stocks.size() - 1;
            View stockItem = createStockItem(stocks.get(i), stockContainer, last);
            stockContainer.addView(stockItem);
        }
    }
    private View createStockItem(Stock stock, LinearLayout  stockContainer, boolean last) {
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View item = inflater.inflate(R.layout.item_stock, stockContainer, false);

        // Находим все TextView
        TextView tvStockName = item.findViewById(R.id.stockName);
        TextView tvStockSum = item.findViewById(R.id.stockSum);
        TextView tvStockCurrentPrice = item.findViewById(R.id.stockCurrentPrice);
        TextView tvStockQuantity = item.findViewById(R.id.stockQuantity);
        TextView tvStockDate = item.findViewById(R.id.stockDate);
        TextView tvStockPurchasePrice = item.findViewById(R.id.stockPurchasePrice);
        TextView tvStockDynamics = item.findViewById(R.id.stockDynamics);

        // Устанавливаем данные
        tvStockName.setText(stock.getStockName());
        String formattedAmount = String.format("%,.0f ₽", stock.getMarketValue())
                .replace(',', ' ');
        tvStockSum.setText(formattedAmount);
        String formattedCurrentPrice = String.format("%,.0f ₽", stock.getCurrentPrice())
                .replace(',', ' ');
        tvStockCurrentPrice.setText(formattedCurrentPrice);
        tvStockQuantity.setText(String.valueOf(stock.getQuantity()));
        tvStockDate.setText(stock.getPurchaseDate());
        String formattedPurchasePrice = String.format("%,.0f ₽", stock.getPurchasePrice())
                .replace(',', ' ');
        tvStockPurchasePrice.setText(formattedPurchasePrice);
        // TODO: 28.05.2025 сделать убытки красными а прибыль зеленой? 
        String formattedDynamics = String.format("%,.2f%%", stock.getReturnPercentage())
                .replace(',', ' ');
        tvStockDynamics.setText(formattedDynamics);

        // Логотип банка
        // TODO: 28.05.2025 сделать их круглыми? 
        ImageView stockIcon = item.findViewById(R.id.stockIcon);

        // Получаем ID иконки из словаря по названию банка
        Integer iconResId = stockIconMap.get(stock.getStockName());

        if (iconResId != null) {
            stockIcon.setImageResource(iconResId);
        } else {
            // Если банк не найден в списке — ставим дефолтную иконку
            stockIcon.setImageResource(R.drawable.bank_default_icon);
        }


        if (!last) {
            LinearLayout stockBody = item.findViewById(R.id.stockBody);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) stockBody.getLayoutParams();
            int marginBottomInPx = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    11,
                    requireContext().getResources().getDisplayMetrics()
            );
            params.setMargins(0, 0, 0, marginBottomInPx);
            stockBody.setLayoutParams(params);
        }

        return item;
    }

    private void initStockIcons() {
        stockIconMap = new HashMap<>();
        stockIconMap.put("Банк ВТБ", R.drawable.vtbr_stock_icon);
        stockIconMap.put("ФосАгро", R.drawable.phor_stock_icon);
        stockIconMap.put("МТС", R.drawable.mtss_stock_icon);
        stockIconMap.put("ЛУКОЙЛ", R.drawable.lkoh_stock_icon);
        stockIconMap.put("Сбер Банк", R.drawable.sber_stock_icon);
        stockIconMap.put("Яндекс", R.drawable.ydex_stock_icon);
        stockIconMap.put("Московская биржа", R.drawable.moex_stock_icon);
    }
}