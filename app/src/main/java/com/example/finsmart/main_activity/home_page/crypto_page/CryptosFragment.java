package com.example.finsmart.main_activity.home_page.crypto_page;

import static java.lang.Double.parseDouble;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
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

public class CryptosFragment extends Fragment {

    private HashMap<String, Integer> cryptoIconMap;
    CryptoDBHelper dbHelper;
    LinearLayout cryptoContainer;
    View fragmentView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.fragment_cryptos, container, false);

        setToolbar(inflater, "Крипта");

        initCryptoIcons();

        dbHelper = new CryptoDBHelper(requireContext());

        // Для заполнения пустой БД небольшим набором данных:
        if (dbHelper.getAllCryptos().isEmpty()) {
            dbHelper.populateInitialData();

        }

        cryptoContainer = fragmentView.findViewById(R.id.cryptoContainer);
        ArrayList<Crypto> cryptos =  getCryptosFromDataBase();
        fillCryptoContainer(cryptos, cryptoContainer);

        Button addCryptoButton = fragmentView.findViewById(R.id.add_crypto_button);
        addCryptoButton.setOnClickListener(v -> showAddCryptoBottomSheet());

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

    ArrayList<Crypto> getCryptosFromDataBase() {
        List<Crypto> cryptoList = dbHelper.getAllCryptos();

        return (ArrayList<Crypto>) cryptoList;
    }

    private void fillCryptoContainer(ArrayList<Crypto> cryptos, LinearLayout cryptoContainer) {
        // Удаляем все существующие элементы
        cryptoContainer.removeAllViews();

        for (int i = 0; i < cryptos.size(); i++) {
            boolean last = i == cryptos.size() - 1;
            View cryptoItem = createCryptoItem(cryptos.get(i), cryptoContainer, last);
            cryptoContainer.addView(cryptoItem);
        }
    }
    private View createCryptoItem(Crypto crypto, LinearLayout  cryptoContainer, boolean last) {
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View item = inflater.inflate(R.layout.item_crypto, cryptoContainer, false);

        // Находим все TextView в item_crypto.xml
        TextView tvCryptoName = item.findViewById(R.id.cryptoName);
        TextView tvCryptoConvertedSum = item.findViewById(R.id.cryptoConvertedSum);
        TextView tvCryptoExchangeRate = item.findViewById(R.id.cryptoExchangeRate);
        TextView tvCryptoQuantity = item.findViewById(R.id.cryptoQuantity);
        TextView tvCryptoPurchaseDate = item.findViewById(R.id.cryptoPurchaseDate);
        TextView tvCryptoPurchaseExchangeRate = item.findViewById(R.id.cryptoPurchaseExchangeRate);
        TextView tvCryptoExchangeRateDynamics = item.findViewById(R.id.cryptoExchangeRateDynamics);
        ImageView ivCryptoIcon = item.findViewById(R.id.cryptoIcon);
        ImageView ivEditButton = item.findViewById(R.id.editButton);

        // Устанавливаем данные
        tvCryptoName.setText(crypto.getName());

        String formattedAmount = String.format("%,.0f ₽", crypto.getMarketValue())
                .replace(',', ' ');
        tvCryptoConvertedSum.setText(formattedAmount);

        String formattedCurrentPrice = String.format("%,.0f ₽", crypto.getCurrentPrice())
                .replace(',', ' ').replace('.', ',');
        tvCryptoExchangeRate.setText(formattedCurrentPrice);

        String roundedQuantity = NumberUtils.roundDecimalPartTo3SD(crypto.getQuantity());
        String formattedQuantity = String.format("%s %s", roundedQuantity, crypto.getSymbol());
        tvCryptoQuantity.setText(formattedQuantity);

        tvCryptoPurchaseDate.setText(crypto.getPurchaseDate());

        String formattedPurchasePrice = String.format("%,.0f ₽", crypto.getBuyInPrice())
                .replace(',', ' ').replace('.', ',');
        tvCryptoPurchaseExchangeRate.setText(formattedPurchasePrice);

        // Обработчик кнопки редактирования
        ivEditButton.setOnClickListener(v -> showEditCryptoBottomSheet(crypto));

        // Раскраска динамики: зелёный — рост, красный — падение
        double returnPercentage = crypto.getReturnPercentage();
        String formattedDynamics = String.format("%,.2f %%", Math.abs(returnPercentage))
                .replace(',', ' ');

        if (returnPercentage >= 0) {
            tvCryptoExchangeRateDynamics.setText("+" + formattedDynamics);
            tvCryptoExchangeRateDynamics.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_green_dark));
        } else {
            tvCryptoExchangeRateDynamics.setText("-" + formattedDynamics);
            tvCryptoExchangeRateDynamics.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark));
        }

        // Логотип криптовалюты
        Integer iconResId = cryptoIconMap.get(crypto.getSymbol()); // или getCode(), если используется код

        if (iconResId != null) {
            ivCryptoIcon.setImageResource(iconResId);
        } else {
            ivCryptoIcon.setImageResource(R.drawable.bank_default_icon); // дефолтная иконка
        }


        if (!last) {
            LinearLayout cryptoBody = item.findViewById(R.id.cryptoBody);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) cryptoBody.getLayoutParams();
            int marginBottomInPx = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    11,
                    requireContext().getResources().getDisplayMetrics()
            );
            params.setMargins(0, 0, 0, marginBottomInPx);
            cryptoBody.setLayoutParams(params);
        }

        return item;
    }

    // TODO: 29.05.2025 Подобрать нормальные, одинаковые по размеру иконки, круглые
    private void initCryptoIcons() {
        cryptoIconMap = new HashMap<>();
        cryptoIconMap.put("BTC", R.drawable.btc_icon);
        cryptoIconMap.put("ETH", R.drawable.eth_icon);
        cryptoIconMap.put("SOL", R.drawable.sol_icon);
        cryptoIconMap.put("TON", R.drawable.ton_icon);
    }

    private void showAddCryptoBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_crypto, null);
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


            // Проверяем, существует ли уже такая крипта
            if (dbHelper.getCryptoByName(name) != null) {
                Toast.makeText(requireContext(), "Криптовалюта с таким названием уже есть", Toast.LENGTH_SHORT).show();
                return;
            }

            // Добавляем в БД
            dbHelper.addCrypto(new Crypto(
                    name,
                    CryptoSymbolMapper.generateSymbol(name), // Например, "USDT" → "USDT"
                    quantity,
                    buyInPrice,
                    getCurrentPriceFromAPI(name), // Здесь можешь получить текущую цену из API
                    purchaseDate
            ));

            // Обновляем UI
            List<Crypto> updatedList = dbHelper.getAllCryptos();
            fillCryptoContainer((ArrayList<Crypto>) updatedList, cryptoContainer);

            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();
    }

    private void showEditCryptoBottomSheet(Crypto crypto) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_crypto, null);
        bottomSheetDialog.setContentView(dialogView);

        TextView cryptoName = dialogView.findViewById(R.id.cryptoName);
        EditText editQuantity = dialogView.findViewById(R.id.editQuantity);
        EditText editPurchaseDate = dialogView.findViewById(R.id.editPurchaseDate);
        EditText editBuyInPrice = dialogView.findViewById(R.id.editBuyInPrice);
        Button buttonSave = dialogView.findViewById(R.id.buttonSave);
        Button buttonDelete = dialogView.findViewById(R.id.buttonDelete);

        // Заполняем поля текущими данными
        cryptoName.setText(crypto.getName());
        editQuantity.setText(String.valueOf(crypto.getQuantity()));
        editBuyInPrice.setText(String.valueOf(crypto.getBuyInPrice()));
        editPurchaseDate.setText(crypto.getPurchaseDate());

        buttonSave.setOnClickListener(v -> {
            String purchaseDate = editPurchaseDate.getText().toString().trim();
            String quantityRaw = editQuantity.getText().toString();
            String buyInPriceRaw = editBuyInPrice.getText().toString();

            if (purchaseDate.isEmpty() || quantityRaw.isEmpty() || buyInPriceRaw.isEmpty() ) {
                Toast.makeText(requireContext(), "Заполните все поля", Toast.LENGTH_SHORT).show();
                return;
            }

            double quantity = parseDouble(quantityRaw);
            double buyInPrice = parseDouble(buyInPriceRaw);

            if (quantity <= 0 || buyInPrice <= 0) {
                Toast.makeText(requireContext(), "Количество и цена должны быть больше нуля", Toast.LENGTH_SHORT).show();
                return;
            }

            // Получаем текущую цену (например, из API или оставляем как есть)

            // Создаём новый объект Crypto с обновлёнными данными
            Crypto updatedCrypto = new Crypto(
                    crypto.getName(),
                    crypto.getSymbol(),
                    quantity,
                    buyInPrice,
                    crypto.getCurrentPrice(), // можно обновить через API, если нужно
                    purchaseDate
            );

            // Обновляем запись в БД по ID
            dbHelper.updateCryptoData(updatedCrypto);

            // Обновляем UI
            List<Crypto> updatedList = dbHelper.getAllCryptos();
            fillCryptoContainer((ArrayList<Crypto>) updatedList, cryptoContainer);

            bottomSheetDialog.dismiss();
        });

        // --- Логика удаления ---
        buttonDelete.setOnClickListener(v -> {
            // Удаляем из БД по коду валюты
            dbHelper.deleteCrypto(crypto);

            // Удаляем из UI
            List<Crypto> updatedList = dbHelper.getAllCryptos();
            fillCryptoContainer((ArrayList<Crypto>) updatedList, cryptoContainer);

            bottomSheetDialog.dismiss();

            // Показываем сообщение
            Toast.makeText(requireContext(), "Валюта удалена", Toast.LENGTH_SHORT).show();
        });

        bottomSheetDialog.show();
    }

    double getCurrentPriceFromAPI(String name) {
        return 3.141592653979;
    }

}