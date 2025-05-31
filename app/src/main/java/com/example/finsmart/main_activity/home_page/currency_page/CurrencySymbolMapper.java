package com.example.finsmart.main_activity.home_page.currency_page;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class CurrencySymbolMapper {

    // Создаем карту соответствий один раз при загрузке класса
    private static final Map<String, String> currencyMap = new HashMap<>();

    // Маппинг: код валюты -> символ (например, "USD" → '$')
    private static final Map<String, Character> currencySymbolMap = new HashMap<>();

    static {
        currencyMap.put("доллар сша", "USD");
        currencyMap.put("евро", "EUR");
        currencyMap.put("фунт стерлингов", "GBP");
        currencyMap.put("йена", "JPY");
        currencyMap.put("юань", "CNY");
        currencyMap.put("рубль", "RUB");
        currencyMap.put("австралийский доллар", "AUD");
        currencyMap.put("канадский доллар", "CAD");
        currencyMap.put("швейцарский франк", "CHF");
        currencyMap.put("новозеландский доллар", "NZD");
        currencyMap.put("российский рубль", "RUB");
        currencyMap.put("сингапурский доллар", "SGD");
        currencyMap.put("гонконгский доллар", "HKD");
        currencyMap.put("корейская вона", "KRW");
        currencyMap.put("мексиканское песо", "MXN");
        currencyMap.put("бразильский реал", "BRL");
        currencyMap.put("индийская рупия", "INR");
        currencyMap.put("южноафриканский рэнд", "ZAR");
        currencyMap.put("турецкая лира", "TRY");
    }
    static {
    // Заполняем маппинг кодов валют к их символам
        currencySymbolMap.put("USD", '$');
        currencySymbolMap.put("EUR", '€');
        currencySymbolMap.put("GBP", '£');
        currencySymbolMap.put("JPY", '¥');
        currencySymbolMap.put("CNY", '¥');
        currencySymbolMap.put("RUB", '₽');
        currencySymbolMap.put("AUD", '$');
        currencySymbolMap.put("CAD", '$');
        currencySymbolMap.put("CHF", '₣');
        currencySymbolMap.put("NZD", '$');
        currencySymbolMap.put("SGD", '$');
        currencySymbolMap.put("HKD", '$');
        currencySymbolMap.put("KRW", '₩');
        currencySymbolMap.put("MXN", '$');
        currencySymbolMap.put("BRL", 'R');
        currencySymbolMap.put("INR", '₹');
        currencySymbolMap.put("ZAR", 'R');
        currencySymbolMap.put("TRY", '₺');
}

    public static String generateCurrencyCode(String name) {
        return currencyMap.get(name.toLowerCase());
    }

    public static char generateSymbol(String name) {
        String code = generateCurrencyCode(name);
        if (code != null) {
            Character symbol = currencySymbolMap.get(code);
            if (symbol != null) {
                return symbol;
            }
        }
        Log.d("tmp", name);      // USD
        // Если не найден — возвращаем знак вопроса или можно бросить исключение
        return '?';
    }

    public static void main(String[] args) {
        System.out.println(generateCurrencyCode("Доллар США"));      // USD
        System.out.println(generateSymbol("Доллар США"));            // $

        System.out.println(generateCurrencyCode("Евро"));             // EUR
        System.out.println(generateSymbol("Евро"));                   // €

        System.out.println(generateCurrencyCode("Рубль"));           // RUB
        System.out.println(generateSymbol("Рубль"));                 // ₽

        System.out.println(generateCurrencyCode("Неизвестная"));      // null
        System.out.println(generateSymbol("Неизвестная"));
    }
}