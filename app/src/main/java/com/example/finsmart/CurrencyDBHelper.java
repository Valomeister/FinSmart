package com.example.finsmart;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class CurrencyDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "currency_database";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_CURRENCY = "currency_table";

    // Названия столбцов
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_CODE = "code";
    private static final String COLUMN_SYMBOL = "symbol";
    private static final String COLUMN_QUANTITY = "quantity";
    private static final String COLUMN_PURCHASE_EXCHANGE_RATE = "purchase_exchange_rate";
    private static final String COLUMN_CURRENT_EXCHANGE_RATE = "current_exchange_rate";
    private static final String COLUMN_PURCHASE_DATE = "purchase_date";

    // SQL для создания таблицы
    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_CURRENCY + " (" +
                    COLUMN_CODE + " TEXT PRIMARY KEY, " +
                    COLUMN_NAME + " TEXT, " +
                    COLUMN_SYMBOL + " CHAR, " +
                    COLUMN_QUANTITY + " REAL, " +
                    COLUMN_PURCHASE_EXCHANGE_RATE + " REAL, " +
                    COLUMN_CURRENT_EXCHANGE_RATE + " REAL, " +
                    COLUMN_PURCHASE_DATE + " TEXT);";

    public CurrencyDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CURRENCY);
        onCreate(db);
    }

    // Добавить валюту
    public void addCurrency(Currency currency) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, currency.getName());
        values.put(COLUMN_CODE, currency.getCode());
        values.put(COLUMN_SYMBOL, (int) currency.getSymbol()); // char -> int
        values.put(COLUMN_QUANTITY, currency.getQuantity());
        values.put(COLUMN_PURCHASE_EXCHANGE_RATE, currency.getPurchaseExchangeRate());
        values.put(COLUMN_CURRENT_EXCHANGE_RATE, currency.getCurrentExchangeRate());
        values.put(COLUMN_PURCHASE_DATE, currency.getPurchaseDate());

        db.insertWithOnConflict(TABLE_CURRENCY, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    // Обновить все данные о валюте
    public void updateCurrency(Currency currency) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, currency.getName());
        values.put(COLUMN_SYMBOL, (int) currency.getSymbol());
        values.put(COLUMN_QUANTITY, currency.getQuantity());
        values.put(COLUMN_PURCHASE_EXCHANGE_RATE, currency.getPurchaseExchangeRate());
        values.put(COLUMN_CURRENT_EXCHANGE_RATE, currency.getCurrentExchangeRate());
        values.put(COLUMN_PURCHASE_DATE, currency.getPurchaseDate());

        db.update(TABLE_CURRENCY, values, COLUMN_CODE + " = ?", new String[]{currency.getCode()});
    }

    // Удалить валюту по коду
    public void deleteCurrency(Currency currency) {
        String code = currency.getCode();
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CURRENCY, COLUMN_CODE + " = ?", new String[]{code});
    }

    // Получить все валюты
    public List<Currency> getAllCurrencies() {
        List<Currency> currencies = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_CURRENCY;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
                String code = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CODE));
                char symbol = (char) cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SYMBOL));
                double quantity = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_QUANTITY));
                double purchaseExchangeRate = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PURCHASE_EXCHANGE_RATE));
                double currentExchangeRate = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_CURRENT_EXCHANGE_RATE));
                String purchaseDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PURCHASE_DATE));

                currencies.add(new Currency(
                        name, code, symbol,
                        quantity, purchaseExchangeRate, currentExchangeRate, purchaseDate
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return currencies;
    }

    // Получить валюту по коду
    public Currency getCurrencyByCode(String code) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CURRENCY,
                new String[]{
                        COLUMN_NAME,
                        COLUMN_CODE,
                        COLUMN_SYMBOL,
                        COLUMN_QUANTITY,
                        COLUMN_PURCHASE_EXCHANGE_RATE,
                        COLUMN_CURRENT_EXCHANGE_RATE,
                        COLUMN_PURCHASE_DATE
                },
                COLUMN_CODE + "=?",
                new String[]{code},
                null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            Currency currency = new Currency(
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CODE)),
                    (char) cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SYMBOL)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_QUANTITY)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PURCHASE_EXCHANGE_RATE)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_CURRENT_EXCHANGE_RATE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PURCHASE_DATE))
            );
            cursor.close();
            return currency;
        }

        if (cursor != null) cursor.close();
        return null;
    }

    // Проверить существование валюты
    public boolean currencyExists(String code) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CURRENCY,
                new String[]{COLUMN_CODE},
                COLUMN_CODE + " = ?",
                new String[]{code}, null, null, null);

        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    // Заполнить тестовыми данными
    public void populateInitialData() {
        List<Currency> sampleCurrencies = getSampleCurrencies();
        for (Currency currency : sampleCurrencies) {
            if (!currencyExists(currency.getCode())) {
                addCurrency(currency);
            }
        }
    }

    // Вспомогательный метод: создаём тестовые данные
    private List<Currency> getSampleCurrencies() {
        ArrayList<Currency> currencies = new ArrayList<>();
        currencies.add(new Currency("Доллар США", "USD", '$', 100.0, 75.0, 74.8, "2024-01-15"));
        currencies.add(new Currency("Евро", "EUR", '€', 50.0, 90.0, 89.5, "2024-02-10"));
        currencies.add(new Currency("Фунт стерлингов ", "GBP", '£', 30.0, 100.0, 101.2, "2024-03-05"));
        currencies.add(new Currency("Йена", "JPY", '¥', 10000.0, 0.65, 0.64, "2024-04-20"));
        return currencies;
    }
}