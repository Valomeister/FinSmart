package com.example.finsmart;

import static java.lang.Double.parseDouble;

import android.content.Context;
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

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StocksFragment extends Fragment {

    private HashMap<String, Integer> stockIconMap;
    StockDBHelper dbHelper;
    LinearLayout stockContainer;
    View fragmentView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.fragment_stocks, container, false);

        setToolbar(inflater, "Акции");

        initStockIcons();

        dbHelper = new StockDBHelper(requireContext());
        // Для заполнения пустой БД небольшим набором данных:
        dbHelper.populateInitialData();

        stockContainer = fragmentView.findViewById(R.id.stockContainer);
        ArrayList<Stock> stocks =  getStocksFromDataBase();
        fillStockContainer(stocks, stockContainer);

        Button addCurrencyButton = fragmentView.findViewById(R.id.add_stock_button);
        addCurrencyButton.setOnClickListener(v -> showAddStockBottomSheet());

        return fragmentView;
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
        ArrayList<Stock> stocks = (ArrayList<Stock>) dbHelper.getAllStocks();

        return stocks;
    }

    private void fillStockContainer(ArrayList<Stock> stocks, LinearLayout stockContainer) {
        // Удаляем все существующие элементы
        stockContainer.removeAllViews();

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
        ImageView ivCurrencyIcon = item.findViewById(R.id.stockIcon);
        ImageView ivEditButton = item.findViewById(R.id.editButton);

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

        // Обработчик кнопки редактирования
        ivEditButton.setOnClickListener(v -> showEditStockBottomSheet(stock));

        // Логотип банка
        // TODO: 28.05.2025 сделать их круглыми?

        // Получаем ID иконки из словаря по названию банка
        Integer iconResId = stockIconMap.get(stock.getStockName());

        if (iconResId != null) {
            ivCurrencyIcon.setImageResource(iconResId);
        } else {
            // Если банк не найден в списке — ставим дефолтную иконку
            ivCurrencyIcon.setImageResource(R.drawable.bank_default_icon);
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

    private void showAddStockBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_stock, null);
        bottomSheetDialog.setContentView(dialogView);

        EditText editName = dialogView.findViewById(R.id.editName);
        EditText editQuantity = dialogView.findViewById(R.id.editQuantity);
        EditText editPurchaseDate = dialogView.findViewById(R.id.editPurchaseDate);
        EditText editBuyInPrice = dialogView.findViewById(R.id.editBuyInPrice);
        Button buttonAdd = dialogView.findViewById(R.id.buttonAdd);

        buttonAdd.setOnClickListener(v -> {
            String name = editName.getText().toString().trim();
            String purchaseDate = editPurchaseDate.getText().toString().trim();

            // Валидация данных
            if (name.isEmpty() || purchaseDate.isEmpty() ) {
                Toast.makeText(requireContext(), "Заполните все поля", Toast.LENGTH_SHORT).show();
                return;
            }

            int quantity = Integer.parseInt(editQuantity.getText().toString());
            double buyInPrice = parseDouble(editBuyInPrice.getText().toString());

            // Валидация данных
            if (quantity <= 0 || buyInPrice <= 0) {
                Toast.makeText(requireContext(), "Количество и цена должны быть больше нуля", Toast.LENGTH_SHORT).show();
                return;
            }

            String stockCode = StockSymbolMapper.getStockCode(name);
            // Проверяем, существует ли уже такая крипта
            if (dbHelper.getStockBySymbol(stockCode) != null) {
                Toast.makeText(requireContext(), "Акция с таким названием уже есть", Toast.LENGTH_SHORT).show();
                return;
            }
            // Добавляем в БД
            dbHelper.addStock(new Stock(
                    name,
                    stockCode,
                    quantity,
                    buyInPrice,
                    getCurrentPriceFromAPI(name), // Здесь можешь получить текущую цену из API
                    purchaseDate
            ));

            // Обновляем UI
            List<Stock> updatedList = dbHelper.getAllStocks();
            fillStockContainer((ArrayList<Stock>) updatedList, stockContainer);

            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();
    }

    private void showEditStockBottomSheet(Stock stock) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_stock, null);
        bottomSheetDialog.setContentView(dialogView);

        TextView stockName = dialogView.findViewById(R.id.stockName);
        EditText editQuantity = dialogView.findViewById(R.id.editQuantity);
        EditText editPurchaseDate = dialogView.findViewById(R.id.editPurchaseDate);
        EditText editBuyInPrice = dialogView.findViewById(R.id.editBuyInPrice);
        Button buttonSave = dialogView.findViewById(R.id.buttonSave);
        Button buttonDelete = dialogView.findViewById(R.id.buttonDelete);

        // Заполняем поля текущими данными
        stockName.setText(stock.getStockName());
        editQuantity.setText(String.valueOf(stock.getQuantity()));
        editBuyInPrice.setText(String.valueOf(stock.getPurchasePrice()));
        editPurchaseDate.setText(stock.getPurchaseDate());

        buttonSave.setOnClickListener(v -> {
            String purchaseDate = editPurchaseDate.getText().toString().trim();
            String quantityRaw = editQuantity.getText().toString();
            String buyInPriceRaw = editBuyInPrice.getText().toString();

            if (purchaseDate.isEmpty() ||
                    quantityRaw.isEmpty() || buyInPriceRaw.isEmpty()) {
                Toast.makeText(requireContext(), "Заполните все поля", Toast.LENGTH_SHORT).show();
                return;
            }

            int quantity = Integer.parseInt(quantityRaw);
            double buyInPrice = parseDouble(buyInPriceRaw);

            if (quantity <= 0 || buyInPrice <= 0) {
                Toast.makeText(requireContext(), "Количество и цена должны быть больше нуля", Toast.LENGTH_SHORT).show();
                return;
            }


            // Создаём новый объект Crypto с обновлёнными данными
            Stock updatedStock = new Stock(
                    stock.getStockName(),
                    stock.getSymbol(), // TODO: 29.05.2025 типо CryptoSymbolMapper.generateSymbol
                    quantity,
                    buyInPrice,
                    getCurrentPriceFromAPI(stock.getStockName()), // можно обновить через API, если нужно
                    purchaseDate
            );

            // Обновляем запись в БД по ID
            dbHelper.updateStock(updatedStock);

            // Обновляем UI
            List<Stock> updatedList = dbHelper.getAllStocks();
            fillStockContainer((ArrayList<Stock>) updatedList, stockContainer);

            bottomSheetDialog.dismiss();
        });

        // --- Логика удаления ---
        buttonDelete.setOnClickListener(v -> {
            // Удаляем из БД по коду валюты
            dbHelper.deleteStock(stock.getSymbol());

            // Удаляем из UI
            List<Stock> updatedList = dbHelper.getAllStocks();
            fillStockContainer((ArrayList<Stock>) updatedList, stockContainer);

            bottomSheetDialog.dismiss();

            // Показываем сообщение
            Toast.makeText(requireContext(), "Акция удалена", Toast.LENGTH_SHORT).show();
        });

        bottomSheetDialog.show();
    }

    double getCurrentPriceFromAPI(String name) {
        return 3.141592653979;
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
