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

public class FundsFragment extends Fragment {

    private HashMap<String, Integer> fundIconMap;
    FundDBHelper dbHelper;
    LinearLayout fundContainer;
    View fragmentView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.fragment_funds, container, false);

        setToolbar(inflater, "Фонды");

        initFundIcons();

        dbHelper = new FundDBHelper(requireContext());
        // Для заполнения пустой БД небольшим набором данных:
        dbHelper.populateInitialData();

        fundContainer = fragmentView.findViewById(R.id.fundContainer);
        ArrayList<Fund> funds =  getFundsFromDataBase();
        fillFundContainer(funds, fundContainer);

        Button addFundButton = fragmentView.findViewById(R.id.add_fund_button);
        addFundButton.setOnClickListener(v -> showAddFundBottomSheet());

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

    ArrayList<Fund> getFundsFromDataBase() {
        ArrayList<Fund> funds = (ArrayList<Fund>) dbHelper.getAllFunds();

        return funds;
    }

    private void fillFundContainer(ArrayList<Fund> funds, LinearLayout fundContainer) {
        // Удаляем все существующие элементы
        fundContainer.removeAllViews();

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
        TextView tvFundDate = item.findViewById(R.id.fundDate);
        TextView tvFundInvestedSum = item.findViewById(R.id.fundInvestedSum);
        TextView tvFundDynamics = item.findViewById(R.id.fundDynamics);
        ImageView ivCurrencyIcon = item.findViewById(R.id.fundIcon);
        ImageView ivEditButton = item.findViewById(R.id.editButton);

        // Устанавливаем данные
        tvFundName.setText(fund.getFundName());
        String formattedAmount = String.format("%,.0f ₽", fund.getAmount())
                .replace(',', ' ');
        tvFundSum.setText(formattedAmount);
        tvFundDynamics.setText(String.format("%.2f%%", fund.getDynamics()).
                replace('.', ','));
        tvFundDate.setText(fund.getStartDate());
        String formattedInvestedSum = String.format("%,.2f ₽", fund.getInvestedSum())
                .replace(',', ' ');
        tvFundInvestedSum.setText(formattedInvestedSum);


        // Обработчик кнопки редактирования
        ivEditButton.setOnClickListener(v -> showEditFundBottomSheet(fund));

        // Получаем ID иконки из словаря по названию банка
        Integer iconResId = fundIconMap.get(fund.getFundName());

        if (iconResId != null) {
            ivCurrencyIcon.setImageResource(iconResId);
        } else {
            // Если банк не найден в списке — ставим дефолтную иконку
            ivCurrencyIcon.setImageResource(R.drawable.bank_default_icon);
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

    private void showAddFundBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_fund, null);
        bottomSheetDialog.setContentView(dialogView);

        EditText editName = dialogView.findViewById(R.id.editName);
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

            double buyInPrice = parseDouble(editBuyInPrice.getText().toString());

            // Валидация данных
            if (buyInPrice <= 0) {
                Toast.makeText(requireContext(), "Цена должны быть больше нуля", Toast.LENGTH_SHORT).show();
                return;
            }

            // Проверяем, существует ли уже такая крипта
            if (dbHelper.getFundByName(name) != null) {
                Toast.makeText(requireContext(), "Фонд с таким названием уже есть", Toast.LENGTH_SHORT).show();
                return;
            }
            double currentFundPrice = buyInPrice * 1.2;
            // Добавляем в БД
            dbHelper.addFund(new Fund(
                    name,
                    getCurrentPriceFromAPI(buyInPrice),
                    buyInPrice,
                    purchaseDate
            ));

            // Обновляем UI
            List<Fund> updatedList = dbHelper.getAllFunds();
            fillFundContainer((ArrayList<Fund>) updatedList, fundContainer);

            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();
    }

    private void showEditFundBottomSheet(Fund fund) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_fund, null);
        bottomSheetDialog.setContentView(dialogView);

        TextView fundName = dialogView.findViewById(R.id.fundName);
        EditText editPurchaseDate = dialogView.findViewById(R.id.editPurchaseDate);
        EditText editBuyInPrice = dialogView.findViewById(R.id.editBuyInPrice);
        Button buttonSave = dialogView.findViewById(R.id.buttonSave);
        Button buttonDelete = dialogView.findViewById(R.id.buttonDelete);

        // Заполняем поля текущими данными
        fundName.setText(fund.getFundName());
        editBuyInPrice.setText(String.valueOf(fund.getInvestedSum()));
        editPurchaseDate.setText(fund.getStartDate());

        buttonSave.setOnClickListener(v -> {
            String purchaseDate = editPurchaseDate.getText().toString().trim();
            String buyInPriceRaw = editBuyInPrice.getText().toString();

            if (purchaseDate.isEmpty() || buyInPriceRaw.isEmpty()) {
                Toast.makeText(requireContext(), "Заполните все поля", Toast.LENGTH_SHORT).show();
                return;
            }

            double buyInPrice = parseDouble(buyInPriceRaw);

            if (buyInPrice <= 0) {
                Toast.makeText(requireContext(), "Цена должны быть больше нуля", Toast.LENGTH_SHORT).show();
                return;
            }


            // Создаём новый объект Crypto с обновлёнными данными
            Fund updatedStock = new Fund(
                    fund.getFundName(),
                    getCurrentPriceFromAPI(buyInPrice), // TODO: 29.05.2025 типо CryptoSymbolMapper.generateSymbol
                    buyInPrice,
                    purchaseDate);

            // Обновляем запись в БД по ID
            dbHelper.updateFund(updatedStock);

            // Обновляем UI
            List<Fund> updatedList = dbHelper.getAllFunds();
            fillFundContainer((ArrayList<Fund>) updatedList, fundContainer);

            bottomSheetDialog.dismiss();
        });

        // --- Логика удаления ---
        buttonDelete.setOnClickListener(v -> {
            // Удаляем из БД по коду валюты
            dbHelper.deleteFund(fund);

            // Удаляем из UI
            List<Fund> updatedList = dbHelper.getAllFunds();
            fillFundContainer((ArrayList<Fund>) updatedList, fundContainer);

            bottomSheetDialog.dismiss();

            // Показываем сообщение
            Toast.makeText(requireContext(), "Акция удалена", Toast.LENGTH_SHORT).show();
        });

        bottomSheetDialog.show();
    }

    double getCurrentPriceFromAPI(double buyInPrice) {
        return buyInPrice * 1.15;
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