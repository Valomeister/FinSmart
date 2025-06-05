package com.example.finsmart.main_activity.home_page.stock_page;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class StockDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "stock_database";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_STOCK = "stock_table";

    // Названия столбцов
    private static final String COLUMN_SYMBOL = "symbol";       // Аббревиатура: например, VTBR
    private static final String COLUMN_NAME = "name";           // Полное название акции
    private static final String COLUMN_QUANTITY = "quantity";   // Количество
    private static final String COLUMN_PURCHASE_PRICE = "purchase_price"; // Цена покупки
    private static final String COLUMN_CURRENT_PRICE = "current_price";   // Текущая цена
    private static final String COLUMN_PURCHASE_DATE = "purchase_date";   // Дата покупки

    // SQL для создания таблицы
    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_STOCK + " (" +
                    COLUMN_SYMBOL + " TEXT PRIMARY KEY, " +
                    COLUMN_NAME + " TEXT, " +
                    COLUMN_QUANTITY + " INTEGER, " +
                    COLUMN_PURCHASE_PRICE + " REAL, " +
                    COLUMN_CURRENT_PRICE + " REAL, " +
                    COLUMN_PURCHASE_DATE + " TEXT);";

    public StockDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STOCK);
        onCreate(db);
    }

    // Добавить акцию
    public void addStock(Stock stock) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SYMBOL, stock.getSymbol());              // добавляем символ
        values.put(COLUMN_NAME, stock.getStockName());
        values.put(COLUMN_QUANTITY, stock.getQuantity());
        values.put(COLUMN_PURCHASE_PRICE, stock.getPurchasePrice());
        values.put(COLUMN_CURRENT_PRICE, stock.getCurrentPrice());
        values.put(COLUMN_PURCHASE_DATE, stock.getPurchaseDate());

        db.insertWithOnConflict(TABLE_STOCK, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    // Обновить данные акции
    public void updateStock(Stock stock) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, stock.getStockName());
        values.put(COLUMN_QUANTITY, stock.getQuantity());
        values.put(COLUMN_PURCHASE_PRICE, stock.getPurchasePrice());
        values.put(COLUMN_CURRENT_PRICE, stock.getCurrentPrice());
        values.put(COLUMN_PURCHASE_DATE, stock.getPurchaseDate());

        db.update(TABLE_STOCK, values, COLUMN_SYMBOL + " = ?", new String[]{stock.getSymbol()});
    }

    // Удалить акцию по символу
    public void deleteStock(String symbol) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_STOCK, COLUMN_SYMBOL + " = ?", new String[]{symbol});
    }

    // Получить все акции
    public List<Stock> getAllStocks() {
        List<Stock> stocks = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_STOCK;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
                String symbol = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SYMBOL));
                int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUANTITY));
                double purchasePrice = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PURCHASE_PRICE));
                double currentPrice = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_CURRENT_PRICE));
                String purchaseDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PURCHASE_DATE));

                stocks.add(new Stock(
                        name, symbol, quantity, purchasePrice, currentPrice, purchaseDate
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return stocks;
    }

    // Получить акцию по символу
    public Stock getStockBySymbol(String symbol) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_STOCK,
                new String[]{
                        COLUMN_SYMBOL,
                        COLUMN_NAME,
                        COLUMN_QUANTITY,
                        COLUMN_PURCHASE_PRICE,
                        COLUMN_CURRENT_PRICE,
                        COLUMN_PURCHASE_DATE
                },
                COLUMN_SYMBOL + "=?",
                new String[]{symbol},
                null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            Stock stock = new Stock(
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SYMBOL)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUANTITY)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PURCHASE_PRICE)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_CURRENT_PRICE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PURCHASE_DATE))
            );
            cursor.close();
            return stock;
        }

        if (cursor != null) cursor.close();
        return null;
    }

    // Проверить, существует ли акция
    public boolean stockExists(String symbol) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_STOCK,
                new String[]{COLUMN_SYMBOL},
                COLUMN_SYMBOL + " = ?",
                new String[]{symbol}, null, null, null);

        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    // Заполнить тестовыми данными
    public void populateInitialData() {
        List<Stock> sampleStocks = getSampleStocks();
        for (Stock stock : sampleStocks) {
            if (!stockExists(stock.getSymbol())) {
                addStock(stock);
            }
        }
    }

    // Вспомогательный метод: создаём тестовые данные
    private List<Stock> getSampleStocks() {
        ArrayList<Stock> stocks = new ArrayList<>();

//        stocks.add(new Stock("Банк ВТБ", "VTBR", 1512, 75.20, 93.59, "19.12.2024"));
//        stocks.add(new Stock("ФосАгро", "PHOR", 1242, 80.45, 72.30, "15.11.2024"));
//        stocks.add(new Stock("МТС", "MTSS", 1421, 68.75, 81.00, "10.01.2025"));
//        stocks.add(new Stock("ЛУКОЙЛ", "LKOH", 1012, 100.00, 90.00, "05.02.2025"));
        stocks.add(new Stock("Сбербанк", "SBER", 1375, 77.32, 93.59, "19.12.2024"));
//        stocks.add(new Stock("Яндекс", "YNDX", 1198, 85.00, 78.40, "01.03.2025"));
//        stocks.add(new Stock("Московская биржа", "MOEX", 1660, 70.00, 88.25, "25.12.2024"));

        return stocks;
    }
}