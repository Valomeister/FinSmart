package com.example.finsmart;

import static java.lang.Double.parseDouble;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
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

import org.w3c.dom.Text;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CurrenciesFragment extends Fragment {

    private HashMap<String, Integer> currencyIconMap;
    CurrencyDBHelper dbHelper;
    LinearLayout currencyContainer;
    View fragmentView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.fragment_currencies, container, false);

        setToolbar(inflater, "Валюта");

        initCurrencyIcons();

        dbHelper = new CurrencyDBHelper(requireContext());
        // Для заполнения пустой БД небольшим набором данных:
        dbHelper.populateInitialData();

        currencyContainer = fragmentView.findViewById(R.id.currencyContainer);
        ArrayList<Currency> currencies =  getCurrenciesFromDataBase();
        fillCurrencyContainer(currencies, currencyContainer);

        Button addCurrencyButton = fragmentView.findViewById(R.id.add_currency_button);
        addCurrencyButton.setOnClickListener(v -> showAddCurrencyBottomSheet());

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

    ArrayList<Currency> getCurrenciesFromDataBase() {
        ArrayList<Currency> currencies = (ArrayList<Currency>) dbHelper.getAllCurrencies();

        return currencies;
    }

    private void fillCurrencyContainer(ArrayList<Currency> currencies, LinearLayout currencyContainer) {
        // Удаляем все существующие элементы
        currencyContainer.removeAllViews();

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

        // Обработчик кнопки редактирования
        ivEditButton.setOnClickListener(v -> showEditCurrencyBottomSheet(currency));

        // Получаем ID иконки из словаря по названию банка
        Integer iconResId = currencyIconMap.get(currency.getCode());

        if (iconResId != null) {
            ivCurrencyIcon.setImageResource(iconResId);
        } else {
            // Если банк не найден в списке — ставим дефолтную иконку
            ivCurrencyIcon.setImageResource(R.drawable.bank_default_icon);
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

    private void showAddCurrencyBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_currency, null);
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

            double quantity = parseDouble(editQuantity.getText().toString());
            double buyInPrice = parseDouble(editBuyInPrice.getText().toString());

            // Валидация данных
            if (quantity <= 0 || buyInPrice <= 0) {
                Toast.makeText(requireContext(), "Количество и цена должны быть больше нуля", Toast.LENGTH_SHORT).show();
                return;
            }

            String currencyCode = CurrencySymbolMapper.generateCurrencyCode(name);
            // Проверяем, существует ли уже такая крипта
            if (dbHelper.getCurrencyByCode(currencyCode) != null) {
                Toast.makeText(requireContext(), "Валюта с таким названием уже есть", Toast.LENGTH_SHORT).show();
                return;
            }

            char currencySymbol = CurrencySymbolMapper.generateSymbol(name);
            // Добавляем в БД
            dbHelper.addCurrency(new Currency(
                    name,
                    currencyCode,
                    currencySymbol, // Например, "USDT" → "USDT"
                    quantity,
                    buyInPrice,
                    getCurrentPriceFromAPI(name), // Здесь можешь получить текущую цену из API
                    purchaseDate
            ));

            // Обновляем UI
            List<Currency> updatedList = dbHelper.getAllCurrencies();
            fillCurrencyContainer((ArrayList<Currency>) updatedList, currencyContainer);

            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();
    }

    private void showEditCurrencyBottomSheet(Currency currency) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_currency, null);
        bottomSheetDialog.setContentView(dialogView);

        TextView currencyName = dialogView.findViewById(R.id.currencyName);
        EditText editQuantity = dialogView.findViewById(R.id.editQuantity);
        EditText editPurchaseDate = dialogView.findViewById(R.id.editPurchaseDate);
        EditText editBuyInPrice = dialogView.findViewById(R.id.editBuyInPrice);
        Button buttonSave = dialogView.findViewById(R.id.buttonSave);
        Button buttonDelete = dialogView.findViewById(R.id.buttonDelete);

        // Заполняем поля текущими данными
        currencyName.setText(currency.getName());
        editQuantity.setText(String.valueOf(currency.getQuantity()));
        editBuyInPrice.setText(String.valueOf(currency.getPurchaseExchangeRate()));
        editPurchaseDate.setText(currency.getPurchaseDate());

        buttonSave.setOnClickListener(v -> {
            String purchaseDate = editPurchaseDate.getText().toString().trim();
            String quantityRaw = editQuantity.getText().toString();
            String buyInPriceRaw = editBuyInPrice.getText().toString();

            if (purchaseDate.isEmpty() ||
                    quantityRaw.isEmpty() || buyInPriceRaw.isEmpty()) {
                Toast.makeText(requireContext(), "Заполните все поля", Toast.LENGTH_SHORT).show();
                return;
            }

            double quantity = parseDouble(quantityRaw);
            double buyInPrice = parseDouble(buyInPriceRaw);

            if (quantity <= 0 || buyInPrice <= 0) {
                Toast.makeText(requireContext(), "Количество и цена должны быть больше нуля", Toast.LENGTH_SHORT).show();
                return;
            }


            // Создаём новый объект Crypto с обновлёнными данными
            Currency updatedCurrency = new Currency(
                    currency.getName(),
                    currency.getCode(), // TODO: 29.05.2025 типо CryptoSymbolMapper.generateSymbol
                    currency.getSymbol(),
                    quantity,
                    buyInPrice,
                    currency.getCurrentExchangeRate(), // можно обновить через API, если нужно
                    purchaseDate
            );

            // Обновляем запись в БД по ID
            dbHelper.updateCurrency(updatedCurrency);

            // Обновляем UI
            List<Currency> updatedList = dbHelper.getAllCurrencies();
            fillCurrencyContainer((ArrayList<Currency>) updatedList, currencyContainer);

            bottomSheetDialog.dismiss();
        });

        // --- Логика удаления ---
        buttonDelete.setOnClickListener(v -> {
            // Удаляем из БД по коду валюты
            dbHelper.deleteCurrency(currency);

            // Удаляем из UI
            List<Currency> updatedList = dbHelper.getAllCurrencies();
            fillCurrencyContainer((ArrayList<Currency>) updatedList, currencyContainer);

            bottomSheetDialog.dismiss();

            // Показываем сообщение
            Toast.makeText(requireContext(), "Валюта удалена", Toast.LENGTH_SHORT).show();
        });

        bottomSheetDialog.show();
    }

    double getCurrentPriceFromAPI(String name) {
        return 3.141592653979;
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